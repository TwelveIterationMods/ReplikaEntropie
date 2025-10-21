package net.blay09.mods.replikaentropie.core.abilities;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class SlowphaseAbility implements Ability {

    public static final SlowphaseAbility INSTANCE = new SlowphaseAbility();
    public static final ResourceLocation ID = id("slowphase");

    private record Slowphaseable(float burstCost) {
        public static final Slowphaseable WATER = new Slowphaseable(0.2f);
        public static final Slowphaseable LAVA = new Slowphaseable(0.5f);
    }

    protected SlowphaseAbility() {
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
                transformBlock(level, pos, target);
                spawnFootParticles(level, player, target == Slowphaseable.LAVA ? ParticleTypes.SMOKE : ParticleTypes.SNOWFLAKE);
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

        return ReplikaArmor.hasPart(player, ArmorItem.Type.BOOTS, ModItems.slowphasers);
    }

    @Nullable
    private static Slowphaseable findTargetAt(Level level, BlockPos pos) {
        final var fluidState = level.getFluidState(pos);
        if (fluidState.is(FluidTags.LAVA)) {
            return Slowphaseable.LAVA;
        } else if (fluidState.is(FluidTags.WATER)) {
            return Slowphaseable.WATER;
        } else {
            return null;
        }
    }

    private static void transformBlock(Level level, BlockPos pos, Slowphaseable target) {
        if (target == Slowphaseable.WATER) {
            level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        } else if (target == Slowphaseable.LAVA) {
            level.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
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
