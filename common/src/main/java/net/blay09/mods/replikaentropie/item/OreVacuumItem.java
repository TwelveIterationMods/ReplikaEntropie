package net.blay09.mods.replikaentropie.item;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.recipe.VacuumableOreRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayDeque;

public class OreVacuumItem extends Item {

    private static final int TICKS_PER_BLOCK = 4;
    private static final float COST_PER_BLOCK = 8f;

    public OreVacuumItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (!(living instanceof Player player)) {
            return;
        }

        if (living.getTicksUsingItem() % TICKS_PER_BLOCK != 0) {
            return;
        }

        final var blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        final var pos = blockHitResult.getBlockPos();
        final var state = level.getBlockState(pos);
        final var foundRecipe = VacuumableOreRecipe.getRecipe(level, state);
        if (foundRecipe.isEmpty()) {
            return;
        }

        if (!BurstEnergy.consumeEnergy(player, COST_PER_BLOCK)) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final var furthestPos = findFurthestMatchingOreInRange(level, pos, state);
        final var drops = Block.getDrops(level.getBlockState(furthestPos), serverLevel, furthestPos, level.getBlockEntity(furthestPos));
        final var recipe = foundRecipe.get();
        level.setBlockAndUpdate(furthestPos, recipe.emptyBlock().defaultBlockState());

        final var spawnDestroyParticlesType = 2001;
        serverLevel.levelEvent(spawnDestroyParticlesType, furthestPos, Block.getId(state));
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.25f, 0.9f + serverLevel.random.nextFloat() * 0.2f);

        // Trail from ore to player
        final var startX = furthestPos.getX() + 0.5;
        final var startY = furthestPos.getY() + 0.5;
        final var startZ = furthestPos.getZ() + 0.5;
        final var endX = player.getX();
        final var endY = player.getEyeY();
        final var endZ = player.getZ();
        final var segments = 6;
        for (int i = 0; i <= segments; i++) {
            final var t = i / (double) segments;
            final var x = startX + (endX - startX) * t;
            final var y = startY + (endY - startY) * t;
            final var z = startZ + (endZ - startZ) * t;
            serverLevel.sendParticles(ParticleTypes.WHITE_ASH, x, y, z, 1, 0, 0, 0, 0);
        }

        for (final var dropItemStack : drops) {
            if (!player.addItem(dropItemStack)) {
                Block.popResource(level, pos, dropItemStack);
            }
        }
    }

    private BlockPos findFurthestMatchingOreInRange(Level level, BlockPos origin, BlockState state) {
        final var mutablePos = new BlockPos.MutableBlockPos();

        final var open = new ArrayDeque<BlockPos>();
        open.add(origin);
        final var closed = new LongOpenHashSet();
        closed.add(origin.asLong());

        final var furthestPos = origin.mutable();
        var maxDistSq = 0.0;

        while (!open.isEmpty()) {
            final var currentPos = open.removeFirst();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        mutablePos.setWithOffset(currentPos, dx, dy, dz);
                        final var neighbourPosLong = mutablePos.asLong();
                        if (closed.contains(neighbourPosLong)) {
                            continue;
                        }

                        final var neighborState = level.getBlockState(mutablePos);
                        if (!neighborState.is(state.getBlock())) {
                            continue;
                        }

                        closed.add(neighbourPosLong);
                        open.add(mutablePos.immutable());

                        final var distSq = origin.distSqr(mutablePos);
                        if (distSq > maxDistSq) {
                            maxDistSq = distSq;
                            furthestPos.set(mutablePos);
                        }
                    }
                }
            }
        }

        return furthestPos;
    }

}
