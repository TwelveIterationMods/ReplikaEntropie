package net.blay09.mods.replikaentropie.item;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.network.protocol.ParticleTrailMessage;
import net.blay09.mods.replikaentropie.recipe.VacuumableOreRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Comparator;

public class OreVacuumItem extends Item {

    private static final int TICKS_PER_BLOCK = 4;
    private static final int TICKS_PER_SHEAR = 12;
    private static final int DURABILITY_COST = 1;
    private static final double SHEAR_SWEEP_RADIUS = 1.25;

    public OreVacuumItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (!(living instanceof Player player)) {
            return;
        }

        if (!ItemDurability.hasCharges(stack)) {
            living.stopUsingItem();
            return;
        }

        if (living.getTicksUsingItem() % TICKS_PER_BLOCK != 0) {
            return;
        }

        tryShearEntities(level, player, stack);
        tryVacuumOre(level, player, stack);
    }

    private void tryShearEntities(Level level, Player player, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final var eyePosition = player.getEyePosition();
        final var reach = player.blockInteractionRange();
        final Vec3 lookDirection = player.getViewVector(1f).normalize();
        final var endPosition = eyePosition.add(lookDirection.scale(reach));
        final var shearArea = new AABB(eyePosition, endPosition).inflate(SHEAR_SWEEP_RADIUS);

        final var targets = level.getEntitiesOfClass(LivingEntity.class, shearArea, entity -> {
            if (entity == player || !(entity instanceof Shearable shearable) || !shearable.readyForShearing()) {
                return false;
            }

            final var toEntity = entity.position().subtract(eyePosition);
            final var forwardDistance = toEntity.dot(lookDirection);
            if (forwardDistance < 0 || forwardDistance > reach + entity.getBbWidth()) {
                return false;
            }

            final var closestPoint = eyePosition.add(lookDirection.scale(forwardDistance));
            final var maxDistanceFromCenter = SHEAR_SWEEP_RADIUS + entity.getBbWidth() * 0.5;
            return entity.distanceToSqr(closestPoint) <= maxDistanceFromCenter * maxDistanceFromCenter;
        });

        targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(eyePosition)));
        final var entity = targets.isEmpty() ? null : targets.getFirst();
        if (entity == null || !ItemDurability.spend(stack, player, player.getUsedItemHand(), DURABILITY_COST)) {
            return;
        }

        if (player.getUseItemRemainingTicks() % TICKS_PER_SHEAR == 0) {
            ((Shearable) entity).shear(serverLevel, SoundSource.PLAYERS, stack);
        } else {
            sendTrailParticles(serverLevel, entity.position().add(0, entity.getBbHeight() * 0.5, 0), new Vec3(player.getX(), player.getEyeY(), player.getZ()));
        }
    }

    private void tryVacuumOre(Level level, Player player, ItemStack stack) {
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

        if (!ItemDurability.spend(stack, player, player.getUsedItemHand(), DURABILITY_COST)) {
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
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.25f, 0.9f + serverLevel.getRandom().nextFloat() * 0.2f);

        sendTrailParticles(serverLevel, Vec3.atCenterOf(furthestPos), new Vec3(player.getX(), player.getEyeY(), player.getZ()));

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

    private void sendTrailParticles(ServerLevel serverLevel, Vec3 start, Vec3 end) {
        final var message = new ParticleTrailMessage(start.toVector3f(), end.toVector3f(), 6, ParticleTypes.WHITE_ASH);
        final var midPoint = start.lerp(end, 0.5);
        Balm.networking().sendToTracking(serverLevel, BlockPos.containing(midPoint), message);
    }

}
