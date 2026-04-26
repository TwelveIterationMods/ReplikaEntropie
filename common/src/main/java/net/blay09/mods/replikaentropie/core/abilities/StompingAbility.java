package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class StompingAbility implements Ability {

    public static final StompingAbility INSTANCE = new StompingAbility();
    public static final Identifier ID = id("stomping");

    private static final float MIN_FALL_DISTANCE = 1f;
    private static final float DAMAGE_PER_FALL_DISTANCE = 2f;
    private static final int CRATER_RADIUS = 2;
    private static final float DAMAGE_RADIUS = 4f;

    protected StompingAbility() {
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 10f;
    }

    @Override
    public void tick(Player player, AbilitySourceContext source) {
    }

    @Override
    public boolean isAvailable(ServerPlayer player, AbilitySourceContext source) {
        return AbilityManager.canAffordDurability(source, this);
    }

    public static void initialize() {
        LivingEntityCallback.Fall.Before.EVENT.register(((entity, fallDamage) -> {
            if (entity instanceof Player player) {
                INSTANCE.handleFall(player, entity.fallDistance);
            }
            return fallDamage;
        }));
    }

    private void handleFall(Player player, double fallDistance) {
        if (!AbilityManager.isAbilityActive(player, INSTANCE)
                || fallDistance < MIN_FALL_DISTANCE
                || !player.hasPose(Pose.CROUCHING)) {
            return;
        }

        final var source = AbilityManager.getActiveSource(player, INSTANCE);
        if (source == null) {
            return;
        }

        final var level = player.level();
        final var pos = player.blockPosition();
        if (!AbilityManager.consumeDurability(player, source, this)) {
            return;
        }

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

    private static void damageNearbyEntities(Level level, Player player, BlockPos center, double fallDistance) {
        final var damage = (float) (fallDistance * DAMAGE_PER_FALL_DISTANCE);
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
