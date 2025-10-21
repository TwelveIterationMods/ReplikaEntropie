package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.FabricatorMenu;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FabricatorBlockEntity extends BlockEntity implements BalmContainerProvider {

    private final Container backingContainer = new DefaultContainer(8) {
        @Override
        public void setChanged() {
            FabricatorBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch(slot) {
                case 1 -> itemStack.is(ModItems.scrap);
                case 2 -> itemStack.is(ModItems.biomass);
                case 3 -> itemStack.is(ModItems.fragments);
                default -> false;
            };
        }
    };
    private final Container resultContainer = new SubContainer(backingContainer, 0, 1);
    private final Container scrapContainer = new SubContainer(backingContainer, 1, 2);
    private final Container biomassContainer = new SubContainer(backingContainer, 2, 3);
    private final Container fragmentContainer = new SubContainer(backingContainer, 3, 4);
    private final Container bufferContainer = new SubContainer(backingContainer, 4, 7);

    private static final int OUTPUT_PROCESSING_TICKS = 10;
    private static final int BUFFER_MOVEMENT_TICKS = 1;

    private final Queue<FabricatorRecipe> recipeQueue = new LinkedList<>();
    private final List<FabricatorRecipe> infiniteQueue = new ArrayList<>();
    private int infiniteQueueIndex;
    private int processingTicks;
    private int bufferMovementTicks;
    private List<FabricatorRecipe> recipes;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case FabricatorMenu.DATA_OUTPUT_PROCESSING_TIME -> processingTicks;
                case FabricatorMenu.DATA_MAX_OUTPUT_PROCESSING_TIME -> OUTPUT_PROCESSING_TICKS;
                case FabricatorMenu.DATA_MISSING_SCRAP -> {
                    final var nextRecipe = getNextOutputRecipe();
                    if (nextRecipe == null) {
                        yield 0;
                    }
                    final var scrapInput = scrapContainer.getItem(0);
                    yield scrapInput.getCount() < nextRecipe.scrap() ? 1 : 0;
                }
                case FabricatorMenu.DATA_MISSING_BIOMASS -> {
                    final var nextRecipe = getNextOutputRecipe();
                    if (nextRecipe == null) {
                        yield 0;
                    }
                    final var biomassInput = biomassContainer.getItem(0);
                    yield biomassInput.getCount() < nextRecipe.biomass() ? 1 : 0;
                }
                case FabricatorMenu.DATA_MISSING_FRAGMENTS -> {
                    final var nextRecipe = getNextOutputRecipe();
                    if (nextRecipe == null) {
                        yield 0;
                    }
                    final var fragmentInput = fragmentContainer.getItem(0);
                    yield fragmentInput.getCount() < nextRecipe.fragments() ? 1 : 0;
                }
                default -> {
                    if (index >= FabricatorMenu.DATA_RECIPES_START && index <= FabricatorMenu.DATA_RECIPES_END) {
                        int recipeIndex = index - FabricatorMenu.DATA_RECIPES_START;
                        final var recipe = recipeIndex < recipes.size() ? recipes.get(recipeIndex) : null;
                        if (recipe == null) {
                            yield 0;
                        } else if (isQueuedInfinitely(recipe)) {
                            yield -1;
                        } else {
                            yield getQueueCount(recipe);
                        }
                    }
                    yield 0;
                }
            };
        }

        @Override
        public void set(int index, int value) {
            if (index >= FabricatorMenu.DATA_RECIPES_START && index <= FabricatorMenu.DATA_RECIPES_END) {
                final var recipe = recipes.get(index - FabricatorMenu.DATA_RECIPES_START);
                if (value == -1) {
                    infiniteQueue.add(recipe);
                } else if (value == 0) {
                    infiniteQueue.remove(recipe);
                    recipeQueue.removeIf(it -> it == recipe);
                } else {
                    final var currentCount = getQueueCount(recipe);
                    if (value > currentCount) {
                        for (int i = 0; i < value - currentCount; i++) {
                            recipeQueue.add(recipe);
                        }
                    } else {
                        for (int i = 0; i < currentCount - value; i++) {
                            recipeQueue.remove(recipe);
                        }
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return FabricatorMenu.DATA_COUNT;
        }
    };

    public FabricatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fabricator.get(), pos, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        recipes = FabricatorRecipe.getRecipes(level);
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.fabricator");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new FabricatorMenu(i, inventory, backingContainer, dataAccess, recipes);
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, FabricatorBlockEntity blockEntity) {
        blockEntity.processOutputRecipes();
        blockEntity.moveBufferItems();
    }

    private boolean isQueuedInfinitely(FabricatorRecipe recipe) {
        return infiniteQueue.contains(recipe);
    }

    private int getQueueCount(FabricatorRecipe recipe) {
        return (int) recipeQueue.stream().filter(it -> it == recipe).count();
    }

    private void processOutputRecipes() {
        if (!bufferContainer.getItem(0).isEmpty()) {
            processingTicks = 0;
            return;
        }

        final var currentRecipe = getNextOutputRecipe();
        if (currentRecipe == null) {
            processingTicks = 0;
            return;
        }

        if (!hasEnoughResources(currentRecipe)) {
            processingTicks = 0;
            return;
        }

        processingTicks++;
        if (processingTicks >= OUTPUT_PROCESSING_TICKS) {
            consumeResources(currentRecipe);

            bufferContainer.setItem(0, currentRecipe.result().copy());

            if (!recipeQueue.isEmpty()) {
                recipeQueue.poll();
            } else {
                infiniteQueueIndex = (infiniteQueueIndex + 1) % infiniteQueue.size();
            }

            processingTicks = 0;
            setChanged();
        }
    }

    private FabricatorRecipe getNextOutputRecipe() {
        if (!recipeQueue.isEmpty()) {
            return recipeQueue.peek();
        }

        if (!infiniteQueue.isEmpty()) {
            return infiniteQueue.get(infiniteQueueIndex);
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
                        } else if (ItemStack.isSameItemSameTags(resultItem, currentItem) &&
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
    public void load(CompoundTag tag) {
        super.load(tag);

        processingTicks = tag.getInt("ProcessingTicks");
        bufferMovementTicks = tag.getInt("BufferMovementTicks");
        infiniteQueueIndex = tag.getInt("InfiniteQueueIndex");

        recipeQueue.clear();
        if (tag.contains("RecipeQueue")) {
            final var queueList = tag.getList("RecipeQueue", Tag.TAG_STRING);
            for (int i = 0; i < queueList.size(); i++) {
                final var recipeId = queueList.getString(i);
                if (level != null) {
                    level.getRecipeManager().byKey(new ResourceLocation(recipeId))
                            .ifPresent(recipe -> {
                                if (recipe instanceof FabricatorRecipe fabricatorRecipe) {
                                    recipeQueue.offer(fabricatorRecipe);
                                }
                            });
                }
            }
        }

        infiniteQueue.clear();
        if (tag.contains("InfiniteQueue")) {
            final var infiniteList = tag.getList("InfiniteQueue", Tag.TAG_STRING);
            for (int i = 0; i < infiniteList.size(); i++) {
                final var recipeId = infiniteList.getString(i);
                if (level != null) {
                    level.getRecipeManager().byKey(new ResourceLocation(recipeId))
                            .ifPresent(recipe -> {
                                if (recipe instanceof FabricatorRecipe fabricatorRecipe) {
                                    infiniteQueue.add(fabricatorRecipe);
                                }
                            });
                }
            }
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ProcessingTicks", processingTicks);
        tag.putInt("BufferMovementTicks", bufferMovementTicks);
        tag.putInt("InfiniteQueueIndex", infiniteQueueIndex);

        final var queueList = new ListTag();
        for (final var recipe : recipeQueue) {
            queueList.add(StringTag.valueOf(recipe.getId().toString()));
        }
        tag.put("RecipeQueue", queueList);

        final var infiniteList = new ListTag();
        for (final var recipe : infiniteQueue) {
            infiniteList.add(StringTag.valueOf(recipe.getId().toString()));
        }
        tag.put("InfiniteQueue", infiniteList);
    }

}
