package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.platform.fluid.BalmFluidTankProvider;
import net.blay09.mods.balm.platform.fluid.DefaultFluidTank;
import net.blay09.mods.balm.platform.fluid.FluidTank;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.ContainerUtils;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.menu.BiomassIncubatorMenu;
import net.blay09.mods.replikaentropie.recipe.BiomassIncubatorRecipe;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class BiomassIncubatorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmFluidTankProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    public static final int CONTAINER_SIZE = 7;
    private static final int GROWTH_TICKS = 6000;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int ENERGY_COST_PER_TICK = 100;

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            BiomassIncubatorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return switch (index) {
                case 0 -> stack.is(Items.WATER_BUCKET);
                case 1 -> level != null && BiomassIncubatorRecipe.getRecipe(level, stack).isPresent();
                case 2 -> stack.is(ModItemTags.BIOMASS_INCUBATOR_SOILS);
                default -> false;
            };
        }

        @Override
        public boolean canTakeItem(Container target, int index, ItemStack stack) {
            return switch (index) {
                case 0 -> stack.is(Items.BUCKET);
                case 3, 4, 5, 6 -> true;
                default -> false;
            };
        }
    };

    private final Container waterContainer = new SubContainer(backingContainer, 0, 1);
    private final Container seedsContainer = new SubContainer(backingContainer, 1, 2);
    private final Container soilContainer = new SubContainer(backingContainer, 2, 3);
    private final SubContainer outputContainer = new SubContainer(backingContainer, 3, 7);
    private final DefaultFluidTank waterTank = new DefaultFluidTank(1000) {
        @Override
        public boolean canFill(Fluid fluid) {
            return fluid.isSame(Fluids.WATER);
        }
    };
    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            BiomassIncubatorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }
    };
    private final NonNullList<ItemStack> outputBuffer = NonNullList.create();

    private int growthTicks;

    private boolean isSyncDirty;
    private int ticksSinceSync;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case BiomassIncubatorMenu.DATA_WATER_TANK -> waterTank.getAmount();
                case BiomassIncubatorMenu.DATA_MAX_WATER_TANK -> waterTank.getCapacity();
                case BiomassIncubatorMenu.DATA_GROWTH_TIME -> growthTicks;
                case BiomassIncubatorMenu.DATA_MAX_GROWTH_TIME -> GROWTH_TICKS;
                case BiomassIncubatorMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case BiomassIncubatorMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return BiomassIncubatorMenu.DATA_COUNT;
        }
    };

    public BiomassIncubatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.biomassIncubator.value(), blockPos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomassIncubatorBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        blockEntity.flushOutputBuffer();
        blockEntity.processBuckets();
        blockEntity.processGrowth();
    }

    private void broadcastChanges() {
        ticksSinceSync++;
        if (isSyncDirty || (ticksSinceSync >= 10 && isGrowing())) {
            BalmBlockEntityUtils.sync(this);
            isSyncDirty = false;
            ticksSinceSync = 0;
        }
    }

    private boolean isGrowing() {
        return growthTicks > 0;
    }

    private void processBuckets() {
        final var waterBucket = waterContainer.getItem(0);
        if (waterBucket.is(Items.WATER_BUCKET) && waterTank.getAmount() + 1000 <= waterTank.getCapacity()) {
            waterTank.fill(Fluids.WATER, 1000, false);
            waterContainer.setItem(0, new ItemStack(Items.BUCKET));
            setChanged();
        }
    }

    private boolean hasWater(BiomassIncubatorRecipe recipe) {
        return waterTank.getAmount() >= recipe.water();
    }

    private void processGrowth() {
        if (!outputBuffer.isEmpty()) {
            return;
        }

        final var recipe = getRecipeForSeed();
        if (recipe.isPresent() && hasSoil(recipe.get()) && hasWater(recipe.get())) {
            if (energyStorage.getEnergy() < ENERGY_COST_PER_TICK) {
                return;
            }

            energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_TICK);
            growthTicks++;
            if (growthTicks >= GROWTH_TICKS) {
                completeGrowth();
            }
        } else {
            growthTicks = 0;
        }
    }

    private Optional<BiomassIncubatorRecipe> getRecipeForSeed() {
        final var seedStack = seedsContainer.getItem(0);
        return BiomassIncubatorRecipe.getRecipe(level, seedStack);
    }

    private boolean hasSoil(BiomassIncubatorRecipe recipe) {
        final var soilStack = soilContainer.getItem(0);
        return recipe.soil().test(soilStack);
    }

    private void completeGrowth() {
        final var seedStack = seedsContainer.getItem(0);
        final var recipe = BiomassIncubatorRecipe.getRecipe(level, seedStack);
        if (recipe.isEmpty()) {
            growthTicks = 0;
            return;
        }

        final var biomassIncubatorRecipe = recipe.get();
        waterTank.drain(Fluids.WATER, biomassIncubatorRecipe.water(), false);
        insertOrBuffer(biomassIncubatorRecipe.result().create());
        growthTicks = 0;
        setChanged();
    }

    private void insertOrBuffer(ItemStack itemStack) {
        final var remainingItem = ContainerUtils.insertItem(outputContainer, itemStack, false);
        if (!remainingItem.isEmpty()) {
            outputBuffer.add(remainingItem);
            setChanged();
        }
    }

    private void flushOutputBuffer() {
        boolean changed = false;
        for (int i = 0; i < outputBuffer.size(); ) {
            final var remainingItem = ContainerUtils.insertItem(outputContainer, outputBuffer.get(i), false);
            if (remainingItem.isEmpty()) {
                outputBuffer.remove(i);
                changed = true;
            } else {
                outputBuffer.set(i, remainingItem);
                i++;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        input.child("WaterTank").ifPresent(waterTank::deserialize);
        outputBuffer.clear();
        input.child("OutputBuffer").ifPresent(child -> ContainerHelper.loadAllItems(child, outputBuffer));

        growthTicks = input.getIntOr("GrowthTicks", 0);
        energyStorage.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        waterTank.serialize(output.child("WaterTank"));
        ContainerHelper.saveAllItems(output.child("OutputBuffer"), outputBuffer);

        output.putInt("GrowthTicks", growthTicks);

        energyStorage.serialize(output);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, this::saveAdditional);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return BalmBlockEntityUtils.createUpdatePacket(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.biomass_incubator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BiomassIncubatorMenu(containerId, inventory, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
        return Unit.STREAM_CODEC.cast();
    }

    @Override
    public Unit getScreenOpeningData(ServerPlayer player) {
        return Unit.INSTANCE;
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public FluidTank getFluidTank() {
        return waterTank;
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    public Container getSoilContainer() {
        return soilContainer;
    }

    public Container getSeedsContainer() {
        return seedsContainer;
    }

    public float getGrowthProgress() {
        return Mth.clamp(growthTicks / (float) GROWTH_TICKS, 0f, 1f);
    }
}
