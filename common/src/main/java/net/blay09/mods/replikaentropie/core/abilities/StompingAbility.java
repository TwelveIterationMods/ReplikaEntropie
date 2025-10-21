package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.LivingFallEvent;
import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class StompingAbility implements Ability {

    public static final StompingAbility INSTANCE = new StompingAbility();
    public static final ResourceLocation ID = id("stomping");

    private static final float MIN_FALL_DISTANCE = 1f;
    private static final float DAMAGE_PER_FALL_DISTANCE = 2f;
    private static final int CRATER_RADIUS = 2;
    private static final float DAMAGE_RADIUS = 4f;

    protected StompingAbility() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 10f;
    }

    @Override
    public void tick(Player player) {
    }

    @Override
    public boolean isAvailable(ServerPlayer player) {
        return ReplikaArmor.hasPart(player, ArmorItem.Type.BOOTS, ModItems.stompers)
                && AbilityManager.canAffordBurst(player, this);
    }

    public static void initialize(BalmEvents events) {
        events.onEvent(LivingFallEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                INSTANCE.handleFall(player, event.getEntity().fallDistance);
            }
        });
    }

    private void handleFall(Player player, float fallDistance) {
        if (!AbilityManager.isAbilityActive(player, INSTANCE)
                || fallDistance < MIN_FALL_DISTANCE
                || !player.hasPose(Pose.CROUCHING)) {
            return;
        }

        final var level = player.level();
        final var pos = player.blockPosition();
        AbilityManager.consumeBurst(player, this);

        if (!level.isClientSide()) {
            createCrater(level, pos, CRATER_RADIUS);
            damageNearbyEntities(level, player, pos, fallDistance);
        }
    }

    private static void createCrater(Level level, BlockPos center, int radius) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        var breakSound = SoundEvents.STONE_BREAK;
        final var pos = center.mutable();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                final var distance = Math.sqrt(x * x + z * z);
                if (distance <= radius) {
                    pos.setWithOffset(center, x, -1, z);
                    final var state = level.getBlockState(pos);

                    if (canStomp(level, pos, state)) {
                        breakSound = state.getSoundType().getBreakSound();

                        final var abovePos = pos.above();
                        final var aboveState = level.getBlockState(abovePos);
                        if (aboveState.canBeReplaced() && canStomp(level, abovePos, aboveState)) {
                            Block.dropResources(aboveState, serverLevel, abovePos, level.getBlockEntity(abovePos));
                            level.removeBlock(abovePos, false);
                        }

                        // We drop resources manually because destroyBlock plays a sound and it gets loud
                        Block.dropResources(state, serverLevel, pos, level.getBlockEntity(pos));
                        level.removeBlock(pos, false);
                    }
                }
            }
        }

        serverLevel.playSound(null, center, breakSound, SoundSource.PLAYERS, 1f, 0.8f);
    }

    private static boolean canStomp(Level level, BlockPos pos, BlockState state) {
        return !state.isAir() && state.getDestroySpeed(level, pos) >= 0f && !state.is(ModBlockTags.IMMUNE_TO_STOMPING);
    }

    private static void damageNearbyEntities(Level level, Player player, BlockPos center, float fallDistance) {
        final var damage = fallDistance * DAMAGE_PER_FALL_DISTANCE;
        final var damageArea = new AABB(center).inflate(DAMAGE_RADIUS);
        final var entities = level.getEntitiesOfClass(LivingEntity.class, damageArea);
        final var damageSource = level.damageSources().playerAttack(player);
        for (final var entity : entities) {
            if (entity != player) {
                entity.hurt(damageSource, damage);
            }
        }
    }
}
