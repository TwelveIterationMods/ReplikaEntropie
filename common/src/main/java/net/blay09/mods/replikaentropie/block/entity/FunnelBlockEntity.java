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
        if (sourceTank == null) {
            return;
        }

        for (int slot = 0; slot < sourceTank.getSlotCount(); slot++) {
            if (sourceTank.isEmpty(slot)) {
                continue;
            }
            final Fluid fluid = sourceTank.getFluid(slot);
            final int simulatedDrain = sourceTank.drain(slot, fluid, Integer.MAX_VALUE, true);
            final int accepted = fluidTank.fill(0, fluid, simulatedDrain, true);
            if (accepted > 0) {
                final int drained = sourceTank.drain(slot, fluid, accepted, false);
                fluidTank.fill(0, fluid, drained, false);
                return;
            }
        }
    }

    private void pushToOutput(Level level, BlockPos pos, Direction outputDirection) {
        if (fluidTank.isEmpty(0)) {
            return;
        }

        final var targetBlockEntity = level.getBlockEntity(pos.relative(outputDirection));
        final var targetTank = targetBlockEntity != null
                ? Balm.capabilities().getCapability(targetBlockEntity, outputDirection.getOpposite(), CommonCapabilities.FLUID_TANK)
                : null;
        if (targetTank == null) {
            return;
        }

        final Fluid fluid = fluidTank.getFluid(0);
        for (int slot = 0; slot < targetTank.getSlotCount(); slot++) {
            final int accepted = targetTank.fill(slot, fluid, fluidTank.getAmount(0), true);
            if (accepted > 0) {
                final int drained = fluidTank.drain(0, fluid, accepted, false);
                targetTank.fill(slot, fluid, drained, false);
                return;
            }
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
        public int fill(int slot, Fluid fluid, int maxFill, boolean simulate) {
            return allowFill ? fluidTank.fill(slot, fluid, maxFill, simulate) : 0;
        }

        @Override
        public int drain(int slot, Fluid fluid, int maxDrain, boolean simulate) {
            if (!allowDrain) {
                return 0;
            }

            return fluidTank.drain(slot, fluid, maxDrain, simulate);
        }

        @Override
        public Fluid getFluid(int slot) {
            return fluidTank.getFluid(slot);
        }

        @Override
        public void setFluid(int slot, Fluid fluid, int amount) {
            fluidTank.setFluid(slot, fluid, amount);
        }

        @Override
        public int getAmount(int slot) {
            return fluidTank.getAmount(slot);
        }

        @Override
        public void setAmount(int slot, int amount) {
            fluidTank.setAmount(slot, amount);
        }

        @Override
        public int getCapacity(int slot) {
            return fluidTank.getCapacity(slot);
        }

        @Override
        public boolean canDrain(int slot, Fluid fluid) {
            return allowDrain && fluidTank.canDrain(slot, fluid);
        }

        @Override
        public boolean canFill(int slot, Fluid fluid) {
            return allowFill && fluidTank.canFill(slot, fluid);
        }

        @Override
        public boolean isEmpty(int slot) {
            return fluidTank.isEmpty(slot);
        }

        @Override
        public int getSlotCount() {
            return fluidTank.getSlotCount();
        }
    }
}
