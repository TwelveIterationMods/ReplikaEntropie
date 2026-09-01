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
    public int fill(int slot, Fluid fluid, int maxFill, boolean simulate) {
        return isValidSlot(slot) ? tanks.get(slot).fill(0, fluid, maxFill, simulate) : 0;
    }

    @Override
    public int drain(int slot, Fluid fluid, int maxDrain, boolean simulate) {
        return isValidSlot(slot) ? tanks.get(slot).drain(0, fluid, maxDrain, simulate) : 0;
    }

    @Override
    public Fluid getFluid(int slot) {
        return isValidSlot(slot) ? tanks.get(slot).getFluid(0) : Fluids.EMPTY;
    }

    @Override
    public void setFluid(int slot, Fluid fluid, int amount) {
        if (isValidSlot(slot)) {
            tanks.get(slot).setFluid(0, fluid, amount);
        }
    }

    @Override
    public int getAmount(int slot) {
        return isValidSlot(slot) ? tanks.get(slot).getAmount(0) : 0;
    }

    @Override
    public void setAmount(int slot, int amount) {
        if (isValidSlot(slot)) {
            tanks.get(slot).setAmount(0, amount);
        }
    }

    @Override
    public int getCapacity(int slot) {
        return isValidSlot(slot) ? tanks.get(slot).getCapacity(0) : 0;
    }

    @Override
    public boolean canDrain(int slot, Fluid fluid) {
        return isValidSlot(slot) && tanks.get(slot).canDrain(0, fluid);
    }

    @Override
    public boolean canFill(int slot, Fluid fluid) {
        return isValidSlot(slot) && tanks.get(slot).canFill(0, fluid);
    }

    @Override
    public boolean isEmpty(int slot) {
        return !isValidSlot(slot) || tanks.get(slot).isEmpty(0);
    }

    @Override
    public int getSlotCount() {
        return tanks.size();
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < tanks.size();
    }
}
