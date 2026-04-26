package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.fluid.DefaultFluidTank;
import net.blay09.mods.balm.platform.fluid.FluidTank;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class CompositeFluidTank implements FluidTank {
    private final List<DefaultFluidTank> tanks;

    public CompositeFluidTank(DefaultFluidTank... tanks) {
        this.tanks = List.of(tanks);
    }

    @Override
    public int fill(Fluid fluid, int maxFill, boolean simulate) {
        int filled = 0;
        for (final var tank : tanks) {
            filled += tank.fill(fluid, maxFill - filled, simulate);
            if (filled >= maxFill) {
                break;
            }
        }
        return filled;
    }

    @Override
    public int drain(Fluid fluid, int maxDrain, boolean simulate) {
        int drained = 0;
        for (final var tank : tanks) {
            drained += tank.drain(fluid, maxDrain - drained, simulate);
            if (drained >= maxDrain) {
                break;
            }
        }
        return drained;
    }

    @Override
    public Fluid getFluid() {
        return Fluids.EMPTY;
    }

    @Override
    public void setFluid(Fluid fluid, int amount) {
    }

    @Override
    public int getAmount() {
        return 0;
    }

    @Override
    public void setAmount(int amount) {
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public boolean canDrain(Fluid fluid) {
        return tanks.stream().anyMatch(it -> it.canDrain(fluid));
    }

    @Override
    public boolean canFill(Fluid fluid) {
        return tanks.stream().anyMatch(it -> it.canFill(fluid));
    }

    @Override
    public boolean isEmpty() {
        return tanks.stream().allMatch(DefaultFluidTank::isEmpty);
    }
}
