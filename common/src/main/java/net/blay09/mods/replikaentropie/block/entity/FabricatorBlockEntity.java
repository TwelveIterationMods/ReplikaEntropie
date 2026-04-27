package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.FabricatorMenu;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FabricatorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmEnergyStorageProvider {

    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int ENERGY_COST_PER_TICK = 10;

    private final DefaultContainer backingContainer = new DefaultContainer(8) {
        @Override
        public void setChanged() {
            FabricatorBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch (slot) {
                case 1 -> itemStack.is(ModItems.scrap);
                case 2 -> itemStack.is(ModItems.biomass);
                case 3 -> itemStack.is(ModItems.fragments);
                default -> false;
            };
        }
    };
    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            FabricatorBlockEntity.this.setChanged();
        }
    };
    private final Container resultContainer = new SubContainer(backingContainer, 0, 1);
    private final Container scrapContainer = new SubContainer(backingContainer, 1, 2);
    private final Container biomassContainer = new SubContainer(backingContainer, 2, 3);
    private final Container fragmentContainer = new SubContainer(backingContainer, 3, 4);
    private final Container bufferContainer = new SubContainer(backingContainer, 4, 7);

    private static final int OUTPUT_PROCESSING_TICKS = 10;
    private static final int BUFFER_MOVEMENT_TICKS = 1;

    private final Queue<ResourceKey<Recipe<?>>> recipeQueue = new LinkedList<>();
    private final List<ResourceKey<Recipe<?>>> infiniteQueue = new ArrayList<>();
    private int infiniteQueueIndex;
    private int processingTicks;
    private int bufferMovementTicks;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case FabricatorMenu.DATA_OUTPUT_PROCESSING_TIME -> processingTicks;
                case FabricatorMenu.DATA_MAX_OUTPUT_PROCESSING_TIME -> OUTPUT_PROCESSING_TICKS;
                case FabricatorMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case FabricatorMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                case FabricatorMenu.DATA_MISSING_SCRAP -> {
                    final var nextRecipe = resolveNextOutputRecipe();
                    if (nextRecipe == null) {
                        yield 0;
                    }
                    final var scrapInput = scrapContainer.getItem(0);
                    yield scrapInput.getCount() < nextRecipe.scrap() ? 1 : 0;
                }
                case FabricatorMenu.DATA_MISSING_BIOMASS -> {
                    final var nextRecipe = resolveNextOutputRecipe();
                    if (nextRecipe == null) {
                        yield 0;
                    }
                    final var biomassInput = biomassContainer.getItem(0);
                    yield biomassInput.getCount() < nextRecipe.biomass() ? 1 : 0;
                }
                case FabricatorMenu.DATA_MISSING_FRAGMENTS -> {
                    final var nextRecipe = resolveNextOutputRecipe();
                    if (nextRecipe == null) {
                        yield 0;
                    }
                    final var fragmentInput = fragmentContainer.getItem(0);
                    yield fragmentInput.getCount() < nextRecipe.fragments() ? 1 : 0;
                }
                default -> {
                    if (index >= FabricatorMenu.DATA_RECIPES_START && index <= FabricatorMenu.DATA_RECIPES_END) {
                        int recipeIndex = index - FabricatorMenu.DATA_RECIPES_START;
                        final var recipes = FabricatorRecipe.getRecipes(level);
                        final var recipe = recipeIndex < recipes.size() ? recipes.get(recipeIndex) : null;
                        if (recipe == null) {
                            yield 0;
                        } else if (isQueuedInfinitely(recipe.id())) {
                            yield -1;
                        } else {
                            yield getQueueCount(recipe.id());
                        }
                    }
                    yield 0;
                }
            };
        }

        @Override
        public void set(int index, int value) {
            if (index >= FabricatorMenu.DATA_RECIPES_START && index <= FabricatorMenu.DATA_RECIPES_END) {
                final var recipes = FabricatorRecipe.getRecipes(level);
                final var recipe = recipes.get(index - FabricatorMenu.DATA_RECIPES_START);
                boolean changed = false;
                if (value == -1) {
                    if (!infiniteQueue.contains(recipe.id())) {
                        infiniteQueue.add(recipe.id());
                        changed = true;
                    }
                } else if (value == 0) {
                    changed |= infiniteQueue.remove(recipe.id());
                    changed |= recipeQueue.removeIf(it -> it.equals(recipe.id()));
                } else {
                    final var currentCount = getQueueCount(recipe.id());
                    if (value > currentCount) {
                        for (int i = 0; i < value - currentCount; i++) {
                            recipeQueue.add(recipe.id());
                        }
                        changed = true;
                    } else {
                        for (int i = 0; i < currentCount - value; i++) {
                            changed |= recipeQueue.remove(recipe.id());
                        }
                    }
                }

                if (changed) {
                    FabricatorBlockEntity.this.setChanged();
                }
            }
        }

        @Override
        public int getCount() {
            return FabricatorMenu.DATA_COUNT;
        }
    };

    public FabricatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fabricator.value(), pos, blockState);
    }

    public BalmMenuProvider<Unit> getMenuProvider() {
        return new BalmMenuProvider<>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.fabricator");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                final var recipes = FabricatorRecipe.getRecipes(level);
                return new FabricatorMenu(i, inventory, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition), recipes);
            }

            @Override
            public Unit getScreenOpeningData(ServerPlayer player) {
                return Unit.INSTANCE;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        };
    }

    @Override
    public Container getContainer(Direction side) {
        return side == Direction.DOWN ? resultContainer : backingContainer;
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FabricatorBlockEntity blockEntity) {
        blockEntity.processOutputRecipes();
        blockEntity.moveBufferItems();
    }

    private boolean isQueuedInfinitely(ResourceKey<Recipe<?>> recipeId) {
        return infiniteQueue.contains(recipeId);
    }

    private int getQueueCount(ResourceKey<Recipe<?>> recipeId) {
        return (int) recipeQueue.stream().filter(it -> it.equals(recipeId)).count();
    }

    private void processOutputRecipes() {
        if (!bufferContainer.getItem(0).isEmpty()) {
            processingTicks = 0;
            return;
        }

        final var currentRecipe = resolveNextOutputRecipe();
        if (currentRecipe == null) {
            processingTicks = 0;
            return;
        }

        if (!hasEnoughResources(currentRecipe)) {
            processingTicks = 0;
            return;
        }

        if (energyStorage.getEnergy() < ENERGY_COST_PER_TICK) {
            return;
        }

        energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_TICK);
        processingTicks++;
        if (processingTicks >= OUTPUT_PROCESSING_TICKS) {
            consumeResources(currentRecipe);

            bufferContainer.setItem(0, currentRecipe.result().create());

            if (!recipeQueue.isEmpty()) {
                recipeQueue.poll();
            } else {
                infiniteQueueIndex = (infiniteQueueIndex + 1) % infiniteQueue.size();
            }

            processingTicks = 0;
            setChanged();
        }
    }

    private @Nullable FabricatorRecipe resolveNextOutputRecipe() {
        final var recipeId = getNextOutputRecipe();
        if (recipeId != null && level instanceof ServerLevel serverLevel) {
            final var recipe = serverLevel.recipeAccess().byKey(recipeId).map(RecipeHolder::value).orElse(null);
            if (recipe instanceof FabricatorRecipe fabricatorRecipe) {
                return fabricatorRecipe;
            }
        }

        return null;
    }

    private @Nullable ResourceKey<Recipe<?>> getNextOutputRecipe() {
        if (!recipeQueue.isEmpty()) {
            return recipeQueue.peek();
        }

        if (!infiniteQueue.isEmpty()) {
            return infiniteQueue.get(infiniteQueueIndex % infiniteQueue.size());
        }

        return null;
    }

    private boolean hasEnoughResources(FabricatorRecipe recipe) {
        final var scrapInput = scrapContainer.getItem(0);
        final var biomassInput = biomassContainer.getItem(0);
        final var fragmentInput = fragmentContainer.getItem(0);
        return (recipe.scrap() == 0f || (scrapInput.is(ModItems.scrap) && scrapInput.getCount() >= recipe.scrap())) &&
                (recipe.biomass() == 0f || (biomassInput.is(ModItems.biomass) && biomassInput.getCount() >= recipe.biomass())) &&
                (recipe.fragments() == 0f || (fragmentInput.is(ModItems.fragments) && fragmentInput.getCount() >= recipe.fragments()));
    }

    private void consumeResources(FabricatorRecipe recipe) {
        if (recipe.scrap() > 0) {
            final var scrapInput = scrapContainer.getItem(0);
            scrapInput.shrink(recipe.scrap());
            if (scrapInput.isEmpty()) {
                scrapContainer.setItem(0, ItemStack.EMPTY);
            }
        }
        if (recipe.biomass() > 0) {
            final var biomassInput = biomassContainer.getItem(0);
            biomassInput.shrink(recipe.biomass());
            if (biomassInput.isEmpty()) {
                biomassContainer.setItem(0, ItemStack.EMPTY);
            }
        }
        if (recipe.fragments() > 0) {
            final var fragmentsInput = fragmentContainer.getItem(0);
            fragmentsInput.shrink(recipe.fragments());
            if (fragmentsInput.isEmpty()) {
                fragmentContainer.setItem(0, ItemStack.EMPTY);
            }
        }
    }

    private void moveBufferItems() {
        bufferMovementTicks++;
        if (bufferMovementTicks >= BUFFER_MOVEMENT_TICKS) {
            for (int i = bufferContainer.getContainerSize() - 1; i >= 0; i--) {
                final var currentItem = bufferContainer.getItem(i);
                if (!currentItem.isEmpty()) {
                    if (i == 2) {
                        final var resultItem = resultContainer.getItem(0);
                        if (resultItem.isEmpty()) {
                            resultContainer.setItem(0, currentItem);
                            bufferContainer.setItem(i, ItemStack.EMPTY);
                        } else if (ItemStack.isSameItemSameComponents(resultItem, currentItem) &&
                                resultItem.getCount() + currentItem.getCount() <= resultItem.getMaxStackSize()) {
                            resultItem.grow(currentItem.getCount());
                            bufferContainer.setItem(i, ItemStack.EMPTY);
                        }
                    } else {
                        final var nextSlot = bufferContainer.getItem(i + 1);
                        if (nextSlot.isEmpty()) {
                            bufferContainer.setItem(i + 1, currentItem);
                            bufferContainer.setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
            bufferMovementTicks = 0;
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        processingTicks = input.getIntOr("ProcessingTicks", 0);
        bufferMovementTicks = input.getIntOr("BufferMovementTicks", 0);
        infiniteQueueIndex = input.getIntOr("InfiniteQueueIndex", 0);
        energyStorage.deserialize(input);

        recipeQueue.clear();
        input.listOrEmpty("RecipeQueue", ResourceKey.codec(Registries.RECIPE)).forEach(recipeQueue::add);

        infiniteQueue.clear();
        input.listOrEmpty("InfiniteQueue", ResourceKey.codec(Registries.RECIPE)).forEach(infiniteQueue::add);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putInt("ProcessingTicks", processingTicks);
        output.putInt("BufferMovementTicks", bufferMovementTicks);
        output.putInt("InfiniteQueueIndex", infiniteQueueIndex);
        energyStorage.serialize(output);

        final var queueList = output.list("RecipeQueue", ResourceKey.codec(Registries.RECIPE));
        for (final var recipe : recipeQueue) {
            queueList.add(recipe);
        }

        final var infiniteList = output.list("InfiniteQueue", ResourceKey.codec(Registries.RECIPE));
        for (final var recipe : infiniteQueue) {
            infiniteList.add(recipe);
        }
    }

}
