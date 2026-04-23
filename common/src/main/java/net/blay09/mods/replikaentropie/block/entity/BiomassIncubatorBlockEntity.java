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
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class BiomassIncubatorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmFluidTankProvider, BalmMenuProvider<Unit> {

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
                case 2 ->
                        level != null && BiomassIncubatorRecipe.getRecipe(level, stack).map(it -> it.soil().test(stack)).orElse(false);
                case 3 -> level != null && BiomassIncubatorRecipe.getRecipe(level, stack).isPresent();
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
    private final Container soilContainer = new SubContainer(backingContainer, 2, 3);
    private final Container seedsContainer = new SubContainer(backingContainer, 3, 4);
    private final DefaultFluidTank waterTank = new DefaultFluidTank(3000);

    private final FractionalResource biomass = new FractionalResource(resultContainer, 0, ModItems.biomass);

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
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomassIncubatorBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
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

    private boolean hasWater() {
        return !getFluidTank().isEmpty();
    }

    private void processGrowth() {
        if (hasWater() && hasValidSeed()) {
            growthTicks++;
            if (growthTicks >= GROWTH_TICKS) {
                completeGrowth();
            }
        } else {
            growthTicks = 0;
        }
    }

    private boolean canWater() {
        if (waterTank.getAmount() < 333) {
            return false;
        }

        if (!hasWater()) {
            return true;
        }

        return false;
    }

    private boolean hasValidSeed() {
        final var seedStack = seedsContainer.getItem(0);
        return level != null && BiomassIncubatorRecipe.getRecipe(level, seedStack).isPresent();
    }

    private void completeGrowth() {
        final var seedStack = seedsContainer.getItem(0);
        final var recipe = BiomassIncubatorRecipe.getRecipe(level, seedStack);
        recipe.ifPresent(biomassIncubatorRecipe -> biomass.add(biomassIncubatorRecipe.biomass()));
        growthTicks = 0;
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        input.child("WaterTank").ifPresent(waterTank::deserialize);

        growthTicks = input.getIntOr("GrowthTicks", 0);

        biomass.setFractionalAmount(input.getFloatOr("FractionalBiomass", 0));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        waterTank.serialize(output.child("WaterTank"));

        output.putInt("GrowthTicks", growthTicks);

        output.putFloat("FractionalBiomass", biomass.getFractionalAmount());
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
        return new BiomassIncubatorMenu(containerId, inventory, backingContainer, dataAccess);
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

    public float getGrowthProgress() {
        return Mth.clamp(growthTicks / (float) GROWTH_TICKS, 0f, 1f);
    }
}
