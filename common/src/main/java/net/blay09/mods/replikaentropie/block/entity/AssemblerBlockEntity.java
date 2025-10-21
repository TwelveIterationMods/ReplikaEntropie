package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.AssemblerMenu;
import net.blay09.mods.replikaentropie.recipe.AssemblerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblerBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider {

    private final DefaultContainer backingContainer = new DefaultContainer(7) {
        @Override
        public void setChanged() {
            AssemblerBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch (slot) {
                case 0 -> false;
                case 1 -> itemStack.is(ModItems.assemblyTicket);
                default -> !itemStack.is(ModItems.assemblyTicket);
            };
        }
    };

    private final Container resultContainer = new SubContainer(backingContainer, 0, 1);
    private final Container ticketContainer = new SubContainer(backingContainer, 1, 2);
    private final Container inputContainer = new SubContainer(backingContainer, 2, 7);

    private static final int PROCESSING_TICKS = 60;
    private int processingTicks;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case AssemblerMenu.DATA_PROCESSING_TIME -> processingTicks;
                case AssemblerMenu.DATA_MAX_PROCESSING_TIME -> PROCESSING_TICKS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return AssemblerMenu.DATA_COUNT;
        }
    };

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.assembler.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.assembler");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AssemblerMenu(id, inv, backingContainer, dataAccess);
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public Container getContainer(Direction side) {
        return side == Direction.DOWN ? resultContainer : backingContainer;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AssemblerBlockEntity assembler) {
        assembler.serverTick(level);
    }

    private void serverTick(Level level) {
        final var ticketStack = ticketContainer.getItem(0);
        if (ticketStack.isEmpty() || !ticketStack.hasTag()) {
            processingTicks = 0;
            return;
        }

        final var itemData = ticketStack.getTag();
        final var recipeIdString = itemData != null ? itemData.getString("ReplikaEntropieAssemblerResult") : null;
        if (recipeIdString == null || recipeIdString.isEmpty()) {
            processingTicks = 0;
            return;
        }

        final var recipe = level.getRecipeManager().byKey(new ResourceLocation(recipeIdString)).orElse(null);
        if (!(recipe instanceof AssemblerRecipe assemblerRecipe)) {
            processingTicks = 0;
            return;
        }

        if (!assemblerRecipe.matches(inputContainer, level)) {
            processingTicks = 0;
            return;
        }

        final var resultStack = assemblerRecipe.assemble(inputContainer, level.registryAccess());
        final var output = resultContainer.getItem(0);
        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(output, resultStack)
                    || output.getCount() + resultStack.getCount() >= output.getMaxStackSize()) {
                processingTicks = 0;
                return;
            }
        }

        processingTicks++;
        if (processingTicks >= PROCESSING_TICKS) {
            for (final var countedIngredient : assemblerRecipe.ingredients()) {
                final var ingredient = countedIngredient.ingredient();
                var needed = Math.max(1, countedIngredient.count());
                for (int i = 0; i < inputContainer.getContainerSize() && needed > 0; i++) {
                    final var inputStack = inputContainer.getItem(i);
                    if (!inputStack.isEmpty() && ingredient.test(inputStack)) {
                        var toTake = Math.min(inputStack.getCount(), needed);
                        inputStack.shrink(toTake);
                        if (inputStack.isEmpty()) {
                            inputContainer.setItem(i, ItemStack.EMPTY);
                        }
                        needed -= toTake;
                    }
                }
            }

            final var usesLeft = itemData.getInt("ReplikaEntropieAssemblerUsesLeft");
            if (usesLeft == 1) {
                ticketStack.shrink(1);
                if (ticketStack.isEmpty()) {
                    ticketContainer.setItem(0, ItemStack.EMPTY);
                }
            } else if (usesLeft > 0) {
                itemData.putInt("ReplikaEntropieAssemblerUsesLeft", usesLeft - 1);
            }

            if (output.isEmpty()) {
                resultContainer.setItem(0, resultStack);
            } else {
                output.grow(resultStack.getCount());
            }

            processingTicks = 0;
            setChanged();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, backingContainer.getItems());
        processingTicks = tag.getInt("ProcessingTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putInt("ProcessingTicks", processingTicks);
    }
}
