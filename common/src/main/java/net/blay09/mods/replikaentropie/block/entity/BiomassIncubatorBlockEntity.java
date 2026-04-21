package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.fluid.BalmFluidTankProvider;
import net.blay09.mods.balm.platform.fluid.DefaultFluidTank;
import net.blay09.mods.balm.platform.fluid.FluidTank;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.BiomassIncubatorMenu;
import net.blay09.mods.replikaentropie.recipe.BiomassIncubatorRecipe;
import net.blay09.mods.replikaentropie.util.FractionalResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BiomassIncubatorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmFluidTankProvider, BalmMenuProvider<Unit> {

    private static final int WATERING_TICKS = 100;
    private static final int GROWTH_TICKS = 200;

    private final DefaultContainer backingContainer = new DefaultContainer(8) {
        @Override
        public void setChanged() {
            BiomassIncubatorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return switch (index) {
                case 1 -> stack.is(Items.WATER_BUCKET);
                case 2, 3, 4 -> level != null && BiomassIncubatorRecipe.getRecipe(level, stack).isPresent();
                default -> false;
            };
        }

        @Override
        public boolean canTakeItem(Container target, int index, ItemStack stack) {
            return switch (index) {
                case 0 -> true;
                case 1 -> stack.is(Items.BUCKET);
                default -> false;
            };
        }
    };

    private final Container resultContainer = new SubContainer(backingContainer, 0, 1);
    private final Container outputContainer = new SubContainer(backingContainer, 0, 2);
    private final Container waterContainer = new SubContainer(backingContainer, 1, 2);
    private final Container seedsContainer = new SubContainer(backingContainer, 2, 5);
    private final DefaultContainer soilContainer = new DefaultContainer(3)  {
        @Override
        public void setChanged() {
            BiomassIncubatorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }
    };
    private final DefaultFluidTank waterTank = new DefaultFluidTank(3000);

    private final int[] growthTicks = new int[3];
    private final FractionalResource biomass = new FractionalResource(resultContainer, 0, ModItems.biomass);
    private final int[] waterUsesRemaining = new int[3];

    private int wateringTicks;

    private boolean isSyncDirty;
    private int ticksSinceSync;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case BiomassIncubatorMenu.DATA_WATERING_TIME -> wateringTicks;
                case BiomassIncubatorMenu.DATA_MAX_WATERING_TIME -> WATERING_TICKS;
                case BiomassIncubatorMenu.DATA_WATER_TANK -> waterTank.getAmount();
                case BiomassIncubatorMenu.DATA_MAX_WATER_TANK -> waterTank.getCapacity();
                case BiomassIncubatorMenu.DATA_GROWTH_TIME_1 -> growthTicks[0];
                case BiomassIncubatorMenu.DATA_GROWTH_TIME_2 -> growthTicks[1];
                case BiomassIncubatorMenu.DATA_GROWTH_TIME_3 -> growthTicks[2];
                case BiomassIncubatorMenu.DATA_MAX_GROWTH_TIME -> GROWTH_TICKS;
                case BiomassIncubatorMenu.DATA_FRACTIONAL_BIOMASS -> biomass.getFractionalAmountAsMenuData();
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

        soilContainer.setItem(0, new ItemStack(Items.DIRT));
        soilContainer.setItem(1, new ItemStack(Items.DIRT));
        soilContainer.setItem(2, new ItemStack(Items.DIRT));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomassIncubatorBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        blockEntity.processBuckets();
        blockEntity.processWatering();
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
        for (int growthTick : growthTicks) {
            if (growthTick > 0) {
                return true;
            }
        }

        return false;
    }

    private void processBuckets() {
        final var waterBucket = waterContainer.getItem(0);
        if (waterBucket.is(Items.WATER_BUCKET) && waterTank.getAmount() + 1000 <= waterTank.getCapacity()) {
            waterTank.fill(Fluids.WATER, 1000, false);
            waterContainer.setItem(0, new ItemStack(Items.BUCKET));
            setChanged();
        }
    }

    private void processWatering() {
        if (canWater()) {
            wateringTicks++;

            final var wateringProgress = (float) wateringTicks / WATERING_TICKS;
            if (wateringProgress >= 1f && soilContainer.getItem(2).is(Items.DIRT)) {
                soilContainer.setItem(2, new ItemStack(Items.FARMLAND));
                waterUsesRemaining[2] = 9;
                waterTank.drain(Fluids.WATER, 334, false);
            } else if (wateringProgress >= 0.65f && soilContainer.getItem(1).is(Items.DIRT)) {
                soilContainer.setItem(1, new ItemStack(Items.FARMLAND));
                waterUsesRemaining[1] = 9;
                waterTank.drain(Fluids.WATER, 333, false);
            } else if (wateringProgress >= 0.29f && soilContainer.getItem(0).is(Items.DIRT)) {
                soilContainer.setItem(0, new ItemStack(Items.FARMLAND));
                waterUsesRemaining[0] = 9;
                waterTank.drain(Fluids.WATER, 333, false);
            }

            if (wateringTicks >= WATERING_TICKS) {
                wateringTicks = 0;
                setChanged();
            }
        } else {
            wateringTicks = 0;
        }
    }

    private boolean isSlotWatered(int slotIndex) {
        return soilContainer.getItem(slotIndex).is(Items.FARMLAND);
    }

    private void processGrowth() {
        for (int i = 0; i < 3; i++) {
            if (isSlotWatered(i) && hasValidSeed(i)) {
                growthTicks[i]++;
                if (growthTicks[i] >= GROWTH_TICKS) {
                    completeGrowth(i);
                }
            } else {
                growthTicks[i] = 0;
            }
        }
    }

    private boolean canWater() {
        if (waterTank.getAmount() < 333) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (!isSlotWatered(i)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasValidSeed(int slotIndex) {
        final var seedStack = seedsContainer.getItem(slotIndex);
        return level != null && BiomassIncubatorRecipe.getRecipe(level, seedStack).isPresent();
    }

    private void completeGrowth(int slotIndex) {
        final var seedStack = seedsContainer.getItem(slotIndex);
        final var recipe = BiomassIncubatorRecipe.getRecipe(level, seedStack);
        recipe.ifPresent(biomassIncubatorRecipe -> biomass.add(biomassIncubatorRecipe.biomass()));

        if (soilContainer.getItem(slotIndex).is(Items.FARMLAND)) {
            if (waterUsesRemaining[slotIndex] > 0) {
                waterUsesRemaining[slotIndex]--;
            }
            if (waterUsesRemaining[slotIndex] <= 0) {
                soilContainer.setItem(slotIndex, new ItemStack(Items.DIRT));
            }
        } else {
            soilContainer.setItem(slotIndex, new ItemStack(Items.DIRT));
            waterUsesRemaining[slotIndex] = 0;
        }
        growthTicks[slotIndex] = 0;
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        soilContainer.getItems().clear();
        input.child("Soil").ifPresent(child -> ContainerHelper.loadAllItems(child, soilContainer.getItems()));
        input.child("WaterTank").ifPresent(waterTank::deserialize);

        wateringTicks = input.getIntOr("WateringTicks", 0);
        for (int i = 0; i < 3; i++) {
            waterUsesRemaining[i] = input.getIntOr("WaterUsesRemaining" + i, 0);
        }
        for (int i = 0; i < 3; i++) {
            growthTicks[i] = input.getIntOr("GrowthTicks" + i, 0);
        }

        biomass.setFractionalAmount(input.getFloatOr("FractionalBiomass", 0));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        waterTank.serialize(output.child("WaterTank"));

        output.putInt("WateringTicks", wateringTicks);
        for (int i = 0; i < 3; i++) {
            output.putInt("WaterUsesRemaining" + i, waterUsesRemaining[i]);
        }
        for (int i = 0; i < 3; i++) {
            output.putInt("GrowthTicks" + i, growthTicks[i]);
        }

        output.putFloat("FractionalBiomass", biomass.getFractionalAmount());

        ContainerHelper.saveAllItems(output.child("Soil"), soilContainer.getItems());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.biomass_incubator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BiomassIncubatorMenu(containerId, inventory, backingContainer, soilContainer, dataAccess);
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
    public Container getContainer(Direction side) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (side) {
            case DOWN -> outputContainer;
            default -> backingContainer;
        };
    }

    @Override
    public FluidTank getFluidTank() {
        return waterTank;
    }

    public Container getSoilContainer() {
        return soilContainer;
    }

    public Container getSeedsContainer() {
        return seedsContainer;
    }

    public float getGrowthProgress(int index) {
        return Mth.clamp(growthTicks[index] / (float) GROWTH_TICKS, 0f, 1f);
    }
}
