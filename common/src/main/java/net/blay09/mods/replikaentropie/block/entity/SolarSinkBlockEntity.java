package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.capabilities.CommonCapabilities;
import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class SolarSinkBlockEntity extends BlockEntity implements BalmEnergyStorageProvider {
    private static final int ENERGY_CAPACITY = 1000;
    private static final int ENERGY_GENERATION_RATE = 20;
    private static final int FRAGMENTED_SUN_MULTIPLIER = 10;
    private static final int ENERGY_OUTPUT_RATE = 100;
    private static final int BOOSTED_ENERGY_OUTPUT_RATE = ENERGY_OUTPUT_RATE * FRAGMENTED_SUN_MULTIPLIER;

    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(BOOSTED_ENERGY_OUTPUT_RATE, ENERGY_CAPACITY, 0, 0) {
        @Override
        public void setChanged() {
            SolarSinkBlockEntity.this.setChanged();
        }
    };

    public SolarSinkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.solarSink.value(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarSinkBlockEntity blockEntity) {
        blockEntity.generateEnergy(level);
        blockEntity.pushEnergyDown(level, pos);
    }

    private void generateEnergy(Level level) {
        if (!canSeeSun()) {
            return;
        }

        final int generated = Math.min(getEnergyGenerationRate(), energyStorage.getCapacity() - energyStorage.getEnergy());
        if (generated > 0) {
            energyStorage.setEnergy(energyStorage.getEnergy() + generated);
        }
    }

    private void pushEnergyDown(Level level, BlockPos pos) {
        if (energyStorage.getEnergy() <= 0) {
            return;
        }

        final var blockEntityBelow = level.getBlockEntity(pos.below());
        final var targetStorage = blockEntityBelow != null
                ? Balm.capabilities().getCapability(blockEntityBelow, Direction.UP, CommonCapabilities.ENERGY_STORAGE)
                : null;
        if (targetStorage == null || !targetStorage.canFill()) {
            return;
        }

        final int maxTransfer = Math.min(getEnergyOutputRate(), energyStorage.getEnergy());
        final int accepted = targetStorage.fill(maxTransfer, true);
        if (accepted <= 0) {
            return;
        }

        final int drained = energyStorage.drain(accepted, false);
        if (drained > 0) {
            targetStorage.fill(drained, false);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        energyStorage.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        energyStorage.deserialize(input);
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorage(Direction side) {
        return side == Direction.DOWN ? energyStorage : null;
    }

    public boolean canSeeSun() {
        return hasFragmentedSunAbove() || (level.isBrightOutside() && level.canSeeSky(worldPosition.above()));
    }

    private int getEnergyGenerationRate() {
        return hasFragmentedSunAbove() ? ENERGY_GENERATION_RATE * FRAGMENTED_SUN_MULTIPLIER : ENERGY_GENERATION_RATE;
    }

    private int getEnergyOutputRate() {
        return hasFragmentedSunAbove() ? BOOSTED_ENERGY_OUTPUT_RATE : ENERGY_OUTPUT_RATE;
    }

    private boolean hasFragmentedSunAbove() {
        return level != null && level.getBlockState(worldPosition.above()).is(ModBlocks.fragmentedSun.asBlock());
    }
}
