package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.capabilities.CommonCapabilities;
import net.blay09.mods.balm.platform.fluid.BalmFluidTankProvider;
import net.blay09.mods.balm.platform.fluid.DefaultFluidTank;
import net.blay09.mods.balm.platform.fluid.FluidTank;
import net.blay09.mods.replikaentropie.block.FunnelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class FunnelBlockEntity extends BlockEntity implements BalmFluidTankProvider {
    private static final int FLUID_CAPACITY = 4000;

    private final DefaultFluidTank fluidTank = new DefaultFluidTank(FLUID_CAPACITY) {
        @Override
        public void setChanged() {
            FunnelBlockEntity.this.setChanged();
        }
    };
    private final FluidTank inputAccess = new SidedFluidTankAccess(true, false);
    private final FluidTank outputAccess = new SidedFluidTankAccess(false, true);

    public FunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.funnel.value(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FunnelBlockEntity blockEntity) {
        blockEntity.pushToOutput(level, pos, state.getValue(FunnelBlock.FACING));
        blockEntity.pullFromAbove(level, pos);
    }

    private void pullFromAbove(Level level, BlockPos pos) {
        final var sourceBlockEntity = level.getBlockEntity(pos.above());
        final var sourceTank = sourceBlockEntity != null
                ? Balm.capabilities().getCapability(sourceBlockEntity, Direction.DOWN, CommonCapabilities.FLUID_TANK)
                : null;
        if (sourceTank == null || sourceTank.isEmpty()) {
            return;
        }

        final Fluid fluid = sourceTank.getFluid();
        final int simulatedDrain = sourceTank.drain(fluid, Integer.MAX_VALUE, true);
        if (simulatedDrain <= 0) {
            return;
        }

        final int accepted = fluidTank.fill(fluid, simulatedDrain, true);
        if (accepted <= 0) {
            return;
        }

        final int drained = sourceTank.drain(fluid, accepted, false);
        if (drained > 0) {
            fluidTank.fill(fluid, drained, false);
        }
    }

    private void pushToOutput(Level level, BlockPos pos, Direction outputDirection) {
        if (fluidTank.isEmpty()) {
            return;
        }

        final var targetBlockEntity = level.getBlockEntity(pos.relative(outputDirection));
        final var targetTank = targetBlockEntity != null
                ? Balm.capabilities().getCapability(targetBlockEntity, outputDirection.getOpposite(), CommonCapabilities.FLUID_TANK)
                : null;
        if (targetTank == null) {
            return;
        }

        final Fluid fluid = fluidTank.getFluid();
        final int accepted = targetTank.fill(fluid, fluidTank.getAmount(), true);
        if (accepted <= 0) {
            return;
        }

        final int drained = fluidTank.drain(fluid, accepted, false);
        if (drained > 0) {
            targetTank.fill(fluid, drained, false);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        input.child("FluidTank").ifPresent(fluidTank::deserialize);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        fluidTank.serialize(output.child("FluidTank"));
    }

    @Override
    public @Nullable FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public @Nullable FluidTank getFluidTank(Direction side) {
        final Direction outputDirection = getBlockState().getValue(FunnelBlock.FACING);
        if (side == outputDirection) {
            return outputAccess;
        }

        return inputAccess;
    }

    private class SidedFluidTankAccess implements FluidTank {
        private final boolean allowFill;
        private final boolean allowDrain;

        private SidedFluidTankAccess(boolean allowFill, boolean allowDrain) {
            this.allowFill = allowFill;
            this.allowDrain = allowDrain;
        }

        @Override
        public int fill(Fluid fluid, int maxFill, boolean simulate) {
            return allowFill ? fluidTank.fill(fluid, maxFill, simulate) : 0;
        }

        @Override
        public int drain(Fluid fluid, int maxDrain, boolean simulate) {
            if (!allowDrain) {
                return 0;
            }

            return fluidTank.drain(fluid, maxDrain, simulate);
        }

        @Override
        public Fluid getFluid() {
            return fluidTank.getFluid();
        }

        @Override
        public void setFluid(Fluid fluid, int amount) {
            fluidTank.setFluid(fluid, amount);
        }

        @Override
        public int getAmount() {
            return fluidTank.getAmount();
        }

        @Override
        public void setAmount(int amount) {
            fluidTank.setAmount(amount);
        }

        @Override
        public int getCapacity() {
            return fluidTank.getCapacity();
        }

        @Override
        public boolean canDrain(Fluid fluid) {
            return allowDrain && fluidTank.canDrain(fluid);
        }

        @Override
        public boolean canFill(Fluid fluid) {
            return allowFill && fluidTank.canFill(fluid);
        }

        @Override
        public boolean isEmpty() {
            return fluidTank.isEmpty();
        }
    }
}
