package net.blay09.mods.replikaentropie.core.dataminer;

import net.blay09.mods.balm.api.event.*;
import net.blay09.mods.replikaentropie.block.entity.EntropicDataMinerBlockEntity;
import net.blay09.mods.replikaentropie.worldgen.ModWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;

import java.util.Objects;
import java.util.stream.Stream;

public class LocalEventLog {

    private static final int EVENT_RANGE = 15;

    public static Stream<EntropicDataMinerBlockEntity> findNearbyDataMiners(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getPoiManager().getInRange(it -> it.is(ModWorldGen.ENTROPIC_DATA_MINER_POI), pos, EVENT_RANGE, PoiManager.Occupancy.ANY)
                    .map(record -> serverLevel.getBlockEntity(record.getPos()) instanceof EntropicDataMinerBlockEntity blockEntity
                            ? blockEntity
                            : null
                    ).filter(Objects::nonNull);
        }

        return Stream.empty();
    }

    public static void announceEvent(Level level, BlockPos pos, DataMinedEvent event) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getPoiManager().getInRange(it -> it.is(ModWorldGen.ENTROPIC_DATA_MINER_POI), pos, EVENT_RANGE, PoiManager.Occupancy.ANY)
                    .forEach(record -> {
                        if (serverLevel.getBlockEntity(record.getPos()) instanceof EntropicDataMinerBlockEntity blockEntity) {
                            blockEntity.addEvent(event);
                        }
                    });
        }
    }

    public static void initialize(BalmEvents events) {
        events.onEvent(TossItemEvent.class, event -> {
            final var pos = event.getPlayer().blockPosition();
            announceEvent(event.getPlayer().level(), pos, DataMinedEvent.ofItem(DataMinedEvent.Type.ITEM_TOSSED, event.getItemStack()));
        });

        events.onEvent(PlayerChangedDimensionEvent.class, event -> {
            final var pos = event.getPlayer().blockPosition();
            final var variant = event.getFromDim().location() + "_" + event.getToDim().location();
            // POSTJAM proper label, fitting icon for vanilla dimensions
            announceEvent(event.getPlayer().level(), pos, DataMinedEvent.of(DataMinedEvent.Type.DIMENSION_CHANGED, variant, new ItemStack(Items.END_PORTAL_FRAME)));
        });

        events.onEvent(PlayerRespawnEvent.class, event -> {
            final var pos = event.getNewPlayer().blockPosition();
            announceEvent(event.getNewPlayer().level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.RESPAWNED, event.getNewPlayer()));
        });

        events.onEvent(LivingDamageEvent.class, event -> {
            final var entity = event.getEntity();
            final var pos = entity.blockPosition();
            final var icon = DataMinedEvent.createEntityIcon(entity);
            final var variant = event.getDamageSource().getMsgId();
            // POSTJAM proper label
            announceEvent(entity.level(), pos, DataMinedEvent.of(DataMinedEvent.Type.DAMAGE_TAKEN, variant, icon));
        });

        events.onEvent(ItemCraftedEvent.class, event -> {
            final var pos = event.getPlayer().blockPosition();
            announceEvent(event.getPlayer().level(), pos, DataMinedEvent.ofItem(DataMinedEvent.Type.ITEM_CRAFTED, event.getItemStack()));
        });

        events.onEvent(UseBlockEvent.class, event -> {
            final var level = event.getPlayer().level();
            final var pos = event.getHitResult().getBlockPos();
            final var state = level.getBlockState(pos);
            if (!state.hasBlockEntity() && state.getMenuProvider(level, pos) == null) {
                return;
            }

            final var itemStack = new ItemStack(state.getBlock());
            if (!itemStack.isEmpty()) {
                announceEvent(event.getPlayer().level(), pos, DataMinedEvent.ofItem(DataMinedEvent.Type.BLOCK_USED, itemStack));
            }
        });

        events.onEvent(EntityAddedEvent.class, event -> {
            final var entity = event.getEntity();
            if (!(entity instanceof Mob)) {
                return;
            }

            final var pos = entity.blockPosition();
            announceEvent(entity.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.ENTITY_SPAWNED, entity));
        });

        events.onEvent(LivingFallEvent.class, event -> {
            final var entity = event.getEntity();
            final var pos = entity.blockPosition();
            announceEvent(entity.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.FALLEN, entity));
        });

        events.onEvent(CropGrowEvent.class, event -> {
            final var state = event.getState();
            if (state.getBlock() instanceof CropBlock cropBlock) {
                if (cropBlock.isMaxAge(state)) {
                    final var itemStack = new ItemStack(cropBlock);
                    if (!itemStack.isEmpty()) {
                        announceEvent(event.getLevel(), event.getPos(), DataMinedEvent.ofItem(DataMinedEvent.Type.CROP_GROWN, itemStack));
                    }
                }
            }
        });

        events.onEvent(UseItemEvent.class, event -> {
            final var pos = event.getPlayer().blockPosition();
            final var itemStack = event.getPlayer().getItemInHand(event.getHand());
            announceEvent(event.getLevel(), pos, DataMinedEvent.ofItem(DataMinedEvent.Type.ITEM_USED, itemStack));
        });

        events.onEvent(LivingHealEvent.class, event -> {
            final var level = event.getEntity().level();
            final var pos = event.getEntity().blockPosition();
            announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.HEALTH_REGENERATED, new ItemStack(Items.GOLDEN_APPLE)));
        });

        events.onEvent(BreakBlockEvent.class, event -> {
            final var level = event.getLevel();
            final var pos = event.getPos();
            final var state = level.getBlockState(pos);
            final var itemStack = new ItemStack(state.getBlock());
            if (!itemStack.isEmpty()) {
                announceEvent(level, pos, DataMinedEvent.ofItem(DataMinedEvent.Type.BLOCK_BROKEN, itemStack));
            }
        });

        events.onEvent(LivingDeathEvent.class, event -> {
            final var entity = event.getEntity();
            if (entity instanceof Player player) {
                final var server = entity.getServer();
                if (server != null) {
                    final var showDeathMessages = server.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
                    if (!showDeathMessages) {
                        final var icon = DataMinedEvent.createPlayerIcon(player);
                        final var variant = DataMinedEvent.getEntityVariant(entity) + "_" + event.getDamageSource().getMsgId();
                        final var deathMessage = event.getDamageSource().getLocalizedDeathMessage(entity);
                        announceEvent(entity.level(), entity.blockPosition(), DataMinedEvent.of(DataMinedEvent.Type.PLAYER_DIED, variant, icon, deathMessage));
                    }
                }
            } else {
                final var icon = DataMinedEvent.createEntityIcon(entity);
                final var variant = DataMinedEvent.getEntityVariant(entity) + "_" + event.getDamageSource().getMsgId();
                final var deathMessage = event.getDamageSource().getLocalizedDeathMessage(entity);
                announceEvent(entity.level(), entity.blockPosition(), DataMinedEvent.of(DataMinedEvent.Type.ENTITY_DIED, variant, icon, deathMessage));
            }
        });
    }

    public static void onJumpFromGround(Player player) {
        final var pos = player.blockPosition();
        announceEvent(player.level(), pos, DataMinedEvent.ofEntity(DataMinedEvent.Type.JUMP, player));
    }
}
