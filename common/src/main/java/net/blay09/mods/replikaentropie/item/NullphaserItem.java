package net.blay09.mods.replikaentropie.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class NullphaserItem extends Item {

    private static final int MAX_DISTANCE = 16;

    public NullphaserItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final var level = context.getLevel();
        final var player = context.getPlayer();
        final var itemStack = context.getItemInHand();
        if (!ItemDurability.hasCharges(itemStack)) {
            return InteractionResult.SUCCESS;
        }
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            final var oppositeFace = context.getClickedFace().getOpposite();
            final var clickedPos = context.getClickedPos();
            tryTeleport(serverLevel, serverPlayer, itemStack, context.getHand(), clickedPos, oppositeFace);
        }
        return InteractionResult.SUCCESS;
    }

    private void tryTeleport(ServerLevel level, ServerPlayer player, ItemStack itemStack, InteractionHand hand, BlockPos pos, Direction direction) {
        final var mutablePos = pos.mutable();
        for (int i = 1; i <= MAX_DISTANCE; i++) {
            mutablePos.setWithOffset(mutablePos, direction);
            final var state = level.getBlockState(mutablePos);
            if (!state.blocksMotion()) {
                final var playerAABB = player.getDimensions(player.getPose()).makeBoundingBox(mutablePos.getX() + 0.5f, mutablePos.getY(), mutablePos.getZ() + 0.5f);
                if (level.noCollision(playerAABB)) {
                    if (!ItemDurability.spend(itemStack, player, hand, 1)) {
                        return;
                    }

                    final var startX = player.getX();
                    final var startY = player.getY();
                    final var startZ = player.getZ();
                    level.playSound(null, startX, startY, startZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
                    level.sendParticles(ParticleTypes.PORTAL,
                            startX, startY + player.getBbHeight() * 0.5, startZ,
                            24,
                            0.5, 0.5, 0.5,
                            0.2);

                    final var targetX = mutablePos.getX() + 0.5;
                    final var targetY = mutablePos.getY();
                    final var targetZ = mutablePos.getZ() + 0.5;
                    player.teleportTo(targetX, targetY, targetZ);

                    level.playSound(null, targetX, targetY, targetZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
                    level.sendParticles(ParticleTypes.PORTAL,
                            targetX, targetY + player.getBbHeight() * 0.5, targetZ,
                            24,
                            0.5, 0.5, 0.5,
                            0.2);
                    return;
                }
            }
        }
    }

}
