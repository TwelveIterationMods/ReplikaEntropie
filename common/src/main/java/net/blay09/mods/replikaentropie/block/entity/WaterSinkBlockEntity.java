package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.capabilities.CommonCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class WaterSinkBlockEntity extends BlockEntity {
    private static final int WATER_OUTPUT_RATE = 100;

    public WaterSinkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.waterSink.value(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterSinkBlockEntity blockEntity) {
        blockEntity.pushWaterDown(level, pos);
    }

    private void pushWaterDown(Level level, BlockPos pos) {
        for (BlockPos currentPos = pos.below(); currentPos.getY() >= level.getMinY(); currentPos = currentPos.below()) {
            final var blockEntity = level.getBlockEntity(currentPos);
            final var targetTank = blockEntity != null
                    ? Balm.capabilities().getCapability(blockEntity, Direction.UP, CommonCapabilities.FLUID_TANK)
                    : null;
            if (targetTank != null) {
                if (targetTank.canFill(Fluids.WATER)) {
                    targetTank.fill(Fluids.WATER, WATER_OUTPUT_RATE, false);
                }
                return;
            }

            final var targetState = level.getBlockState(currentPos);
            if (targetState.isFaceSturdy(level, currentPos, Direction.UP) || targetState.isFaceSturdy(level, currentPos, Direction.DOWN)) {
                return;
            }
        }
    }
}
