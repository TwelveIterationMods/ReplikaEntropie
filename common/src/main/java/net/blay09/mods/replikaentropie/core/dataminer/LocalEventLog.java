package net.blay09.mods.replikaentropie.core.dataminer;

import net.blay09.mods.balm.platform.event.callback.*;
import net.blay09.mods.replikaentropie.block.entity.EntropicDataMinerBlockEntity;
import net.blay09.mods.replikaentropie.worldgen.ModPoiTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Objects;
import java.util.stream.Stream;

public class LocalEventLog {

    private static final int EVENT_RANGE = 15;

    public static Stream<EntropicDataMinerBlockEntity> findNearbyDataMiners(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getPoiManager().getInRange(it -> it.is(ModPoiTypes.ENTROPIC_DATA_MINER_POI), pos, EVENT_RANGE, PoiManager.Occupancy.ANY)
                    .map(record -> serverLevel.getBlockEntity(record.getPos()) instanceof EntropicDataMinerBlockEntity blockEntity
                            ? blockEntity
                            : null
                    ).filter(Objects::nonNull);
        }

        return Stream.empty();
    }

    public static void announceEvent(LevelAccessor level, BlockPos pos, DataMinedEvent event) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getPoiManager().getInRange(it -> it.is(ModPoiTypes.ENTROPIC_DATA_MINER_POI), pos, EVENT_RANGE, PoiManager.Occupancy.ANY)
                    .forEach(record -> {
                        if (serverLevel.getBlockEntity(record.getPos()) instanceof EntropicDataMinerBlockEntity blockEntity) {
                            blockEntity.addEvent(event, pos);
                        }
                    });
        }
    }

    public static void initialize() {
        ItemCallback.Toss.Before.EVENT.register(((player, itemStack) -> {
            final var pos = player.blockPosition();
            announceEvent(player.level(), pos, DataMinedEvent.ofItem(DataMinedEvent.Type.ITEM_TOSSED, itemStack));
            return true;
        }));

        ServerPlayerCallback.DimensionChange.EVENT.register(((player, from, to) -> {
            final var pos = player.blockPosition();
            final var variant = from.identifier() + "_" + to.identifier();
            // POSTJAM proper label, fitting icon for vanilla dimensions
            announceEvent(player.level(), pos, DataMinedEvent.of(DataMinedEvent.Type.DIMENSION_CHANGED, variant, new ItemStack(Items.END_PORTAL_FRAME)));
        }));

        ServerPlayerCallback.Respawn.EVENT.register((_, newPlayer) -> {
            final var pos = newPlayer.blockPosition();
            announceEvent(newPlayer.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.RESPAWNED, newPlayer));
        });

        LivingEntityCallback.Damage.Before.EVENT.register(((entity, damageSource, damageAmount) -> {
            final var pos = entity.blockPosition();
            final var icon = DataMinedEvent.createEntityIcon(entity);
            final var variant = damageSource.getMsgId();
            // POSTJAM proper label
            announceEvent(entity.level(), pos, DataMinedEvent.of(DataMinedEvent.Type.DAMAGE_TAKEN, variant, icon));
            return damageAmount;
        }));

        ItemCallback.Craft.After.EVENT.register(((player, itemStack, _) -> {
            final var pos = player.blockPosition();
            announceEvent(player.level(), pos, DataMinedEvent.ofItem(DataMinedEvent.Type.ITEM_CRAFTED, itemStack));
        }));

        BlockCallback.Use.EVENT.register(((_, level, _, hitResult) -> {
            final var pos = hitResult.getBlockPos();
            final var state = level.getBlockState(pos);
            if (!state.hasBlockEntity() && state.getMenuProvider(level, pos) == null) {
                return InteractionEventResult.DEFAULT;
            }

            final var itemStack = new ItemStack(state.getBlock());
            if (!itemStack.isEmpty()) {
                announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.BLOCK_USED, itemStack));
            }
            return InteractionEventResult.DEFAULT;
        }));

        EntityCallback.AddedToLevel.EVENT.register((_, entity) -> {
            if (!(entity instanceof Mob)) {
                return;
            }

            final var pos = entity.blockPosition();
            announceEvent(entity.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.ENTITY_SPAWNED, entity));
        });

        LivingEntityCallback.Fall.Before.EVENT.register(((entity, fallDamage) -> {
            final var pos = entity.blockPosition();
            announceEvent(entity.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.FALLEN, entity));
            return fallDamage;
        }));

        CropCallback.Grow.After.EVENT.register((level, pos, state) -> {
            if (state.getBlock() instanceof CropBlock cropBlock) {
                if (cropBlock.isMaxAge(state)) {
                    final var itemStack = new ItemStack(cropBlock);
                    if (!itemStack.isEmpty()) {
                        announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.CROP_GROWN, itemStack));
                    }
                }
            }
        });

        ItemCallback.Use.EVENT.register(((player, level, hand) -> {
            final var pos = player.blockPosition();
            final var itemStack = player.getItemInHand(hand);
            announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.ITEM_USED, itemStack));
            return InteractionEventResult.DEFAULT;
        }));

        LivingEntityCallback.Heal.Before.EVENT.register(((entity, healAmount) -> {
            final var level = entity.level();
            final var pos = entity.blockPosition();
            announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.HEALTH_REGENERATED, new ItemStack(Items.GOLDEN_APPLE)));
            return healAmount;
        }));

        BlockCallback.Break.Before.EVENT.register(((level, pos, state, _, _) -> {
            final var itemStack = new ItemStack(state.getBlock());
            if (!itemStack.isEmpty()) {
                announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.BLOCK_BROKEN, itemStack));
            }
            return true;
        }));

        LivingEntityCallback.Death.Before.EVENT.register(((entity, damageSource) -> {
            if (entity instanceof Player player) {
                if (entity.level() instanceof ServerLevel serverLevel) {
                    final var showDeathMessages = serverLevel.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES);
                    if (!showDeathMessages) {
                        final var icon = DataMinedEvent.createPlayerIcon(player);
                        final var variant = DataMinedEvent.getEntityVariant(entity) + "_" + damageSource.getMsgId();
                        final var deathMessage = damageSource.getLocalizedDeathMessage(entity);
                        announceEvent(entity.level(), entity.blockPosition(), DataMinedEvent.of(DataMinedEvent.Type.PLAYER_DIED, variant, icon, deathMessage));
                    }
                }
            } else {
                final var icon = DataMinedEvent.createEntityIcon(entity);
                final var variant = DataMinedEvent.getEntityVariant(entity) + "_" + damageSource.getMsgId();
                final var deathMessage = damageSource.getLocalizedDeathMessage(entity);
                announceEvent(entity.level(), entity.blockPosition(), DataMinedEvent.of(DataMinedEvent.Type.ENTITY_DIED, variant, icon, deathMessage));
            }
            return true;
        }));
    }

    public static void onJumpFromGround(Player player) {
        final var pos = player.blockPosition();
        announceEvent(player.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.JUMP, player));
    }
}
