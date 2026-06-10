package net.blay09.mods.replikaentropie.core.crane;

import net.blay09.mods.replikaentropie.api.crane.CranePickupHandler;
import net.blay09.mods.replikaentropie.api.crane.CranePlacementHandler;
import net.blay09.mods.replikaentropie.api.crane.CraneTransfer;
import net.blay09.mods.replikaentropie.api.crane.CraneHandlerRegistry;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockCraneHandlers {

    private BlockCraneHandlers() {
    }

    public static void initialize() {
        CraneHandlerRegistry.registerPickup(new BlockCranePickupHandler());
        CraneHandlerRegistry.registerPlacement(new BlockCranePlacementHandler());
    }

    private static class BlockCranePickupHandler implements CranePickupHandler {
        @Override
        public @Nullable CraneTransfer peekPickup(ServerLevel level, BlockPos sourcePos) {
            final var sourceState = level.getBlockState(sourcePos);
            if (!canMove(level, sourcePos, sourceState)) {
                return null;
            }

            final var sourceBlockEntity = level.getBlockEntity(sourcePos);
            final var blockEntityData = sourceBlockEntity != null ? sourceBlockEntity.saveWithFullMetadata(level.registryAccess()) : null;
            return new CraneTransfer(sourceState, blockEntityData);
        }

        @Override
        public @Nullable CraneTransfer tryPickup(ServerLevel level, BlockPos sourcePos) {
            final var transfer = peekPickup(level, sourcePos);
            if (transfer != null) {
                level.removeBlockEntity(sourcePos);
                level.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
            return transfer;
        }
    }

    private static class BlockCranePlacementHandler implements CranePlacementHandler {
        @Override
        public boolean canPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
            return level.getBlockState(destinationPos).isAir();
        }

        @Override
        public boolean tryPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
            if (!canPlace(level, destinationPos, transfer) || !level.setBlock(destinationPos, transfer.state(), Block.UPDATE_ALL)) {
                return false;
            }

            if (transfer.blockEntityData() != null) {
                final var blockEntity = BlockEntity.loadStatic(destinationPos, level.getBlockState(destinationPos), transfer.blockEntityData(), level.registryAccess());
                if (blockEntity != null) {
                    level.setBlockEntity(blockEntity);
                    blockEntity.setChanged();
                }
            }
            return true;
        }
    }

    private static boolean canMove(ServerLevel level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && state.getFluidState().isEmpty()
                && state.getDestroySpeed(level, pos) >= 0f
                && !state.is(ModBlockTags.CRANE_RELOCATION_NOT_SUPPORTED);
    }
}
