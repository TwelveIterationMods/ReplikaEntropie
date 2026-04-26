package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class MagphaseAbility implements Ability {

    public static final MagphaseAbility INSTANCE = new MagphaseAbility();
    public static final Identifier ID = id("magphase");
    private static final Map<BlockGetter, Set<BlockPos>> magphasedPositionsByLevel = new WeakHashMap<>();

    private record Magphaseable(float durabilityCost) {
        public static final Magphaseable WATER = new Magphaseable(0.15f);
        public static final Magphaseable LAVA = new Magphaseable(0.3f);
    }

    protected MagphaseAbility() {
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 0f;
    }

    @Override
    public void tick(Player player, AbilitySourceContext source) {
        final var level = player.level();
        final var pos = player.blockPosition().below();
        final var target = findTargetAt(level, pos);
        if (target != null) {
            if (AbilityManager.consumeDurability(player, source, target.durabilityCost())) {
                final var magphasedPositions = magphasedPositionsByLevel.computeIfAbsent(level, it -> new HashSet<>());
                magphasedPositions.add(pos);
                spawnFootParticles(level, player, ParticleTypes.ELECTRIC_SPARK);
            }
        }
    }

    @Override
    public boolean isAvailable(ServerPlayer player, AbilitySourceContext source) {
        if (!player.onGround() && player.hasPose(Pose.CROUCHING)) {
            return false;
        }

        if (player.isInWater() || player.isInLava()) {
            return false;
        }

        if (player.fallDistance > 5f && !player.isFallFlying()) {
            return false;
        }

        return AbilityManager.canAffordDurability(source, 1f);
    }

    public static void initialize() {
        // Reset magphased positions globally every tick, before applying new magphase in ability tick
        ServerTickCallback.ServerLevelTick.BEFORE.register(MagphaseAbility::resetMagphasedPositions);
    }

    @Nullable
    private static Magphaseable findTargetAt(Level level, BlockPos pos) {
        final var fluidState = level.getFluidState(pos);
        if (fluidState.is(FluidTags.LAVA)) {
            return Magphaseable.LAVA;
        } else if (fluidState.is(FluidTags.WATER)) {
            return Magphaseable.WATER;
        } else {
            return null;
        }
    }

    public static boolean isPositionMagphased(BlockGetter level, BlockPos pos) {
        final var magphasedPositions = magphasedPositionsByLevel.computeIfAbsent(level, it -> new HashSet<>());
        return magphasedPositions.contains(pos);
    }

    public static void resetMagphasedPositions(BlockGetter level) {
        final var magphasedPositions = magphasedPositionsByLevel.get(level);
        if (magphasedPositions != null) {
            magphasedPositions.clear();
        }
    }

    private static void spawnFootParticles(Level level, Player player, SimpleParticleType particle) {
        if (level instanceof ServerLevel serverLevel) {
            final var x = player.getX();
            final var y = player.getY() + 0.1;
            final var z = player.getZ();
            final var count = 2;
            final var spread = 0.2;
            serverLevel.sendParticles(particle,
                    x, y, z,
                    count,
                    spread, 0, spread,
                    0.01);
        }
    }
}
