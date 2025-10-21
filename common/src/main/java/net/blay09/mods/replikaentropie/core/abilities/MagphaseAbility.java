package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
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
    public static final ResourceLocation ID = id("magphase");
    private static final Map<BlockGetter, Set<BlockPos>> magphasedPositionsByLevel = new WeakHashMap<>();

    private record Magphaseable(float burstCost) {
        public static final Magphaseable WATER = new Magphaseable(0.15f);
        public static final Magphaseable LAVA = new Magphaseable(0.3f);
    }

    protected MagphaseAbility() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 0f;
    }

    @Override
    public void tick(Player player) {
        final var level = player.level();
        final var pos = player.blockPosition().below();
        final var target = findTargetAt(level, pos);
        if (target != null) {
            if (BurstEnergy.consumeEnergy(player, target.burstCost())) {
                final var magphasedPositions = magphasedPositionsByLevel.computeIfAbsent(level, it -> new HashSet<>());
                magphasedPositions.add(pos);
                spawnFootParticles(level, player, ParticleTypes.ELECTRIC_SPARK);
            }
        }
    }

    @Override
    public boolean isAvailable(ServerPlayer player) {
        if (!player.onGround() && player.hasPose(Pose.CROUCHING)) {
            return false;
        }

        if (player.isInWater() || player.isInLava()) {
            return false;
        }

        if (player.fallDistance > 5f && !player.isFallFlying()) {
            return false;
        }

        return ReplikaArmor.hasPart(player, ArmorItem.Type.BOOTS, ModItems.magphasers);
    }

    public static void initialize(BalmEvents events) {
        // Reset magphased positions globally every tick, before applying new magphase in ability tick
        Balm.getEvents().onTickEvent(TickType.ServerLevel, TickPhase.Start, MagphaseAbility::resetMagphasedPositions);
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
