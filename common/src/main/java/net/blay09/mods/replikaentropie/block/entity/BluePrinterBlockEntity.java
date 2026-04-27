package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.*;
import net.blay09.mods.replikaentropie.component.AssemblyTicket;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.BluePrinterMenu;
import net.blay09.mods.replikaentropie.recipe.PreviewableRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Optional;

public class BluePrinterBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit> {
    private static final int PROCESSING_TICKS = 20;

    public static final int CONTAINER_SIZE = 13;
    public static final int PAPER_SLOT = 0;
    public static final int INK_SLOT = 1;
    public static final int CYAN_DYE_SLOT = 2;
    public static final int GRID_START = 3;
    public static final int GRID_END = 12;
    public static final int OUTPUT_SLOT = 12;

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            BluePrinterBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch (slot) {
                case OUTPUT_SLOT -> false;
                case PAPER_SLOT -> itemStack.is(Items.PAPER);
                case INK_SLOT -> itemStack.is(Items.INK_SAC);
                case CYAN_DYE_SLOT -> itemStack.is(Items.CYAN_DYE);
                default -> true;
            };
        }
    };

    private final Container outputContainer = new SubContainer(backingContainer, OUTPUT_SLOT, OUTPUT_SLOT + 1);
    private int processingTicks;

    public BluePrinterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.bluePrinter.value(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.blue_printer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BluePrinterMenu(id, inv, backingContainer, ContainerLevelAccess.create(level, worldPosition));
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
        return side == Direction.DOWN ? outputContainer : backingContainer;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BluePrinterBlockEntity blockEntity) {
        blockEntity.serverTick(level);
    }

    public static Optional<ProcessingResult> computeResult(Level level, Container container) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        final var gridItems = new ArrayList<ItemStack>(9);
        for (int i = GRID_START; i < OUTPUT_SLOT; i++) {
            gridItems.add(container.getItem(i));
        }

        final var ticketResult = computeAssemblyTicketResult(serverLevel, gridItems);
        if (ticketResult.isPresent()) {
            return ticketResult;
        }

        final var craftingInput = CraftingInput.of(3, 3, gridItems);
        return serverLevel.recipeAccess()
                .getRecipeFor(RecipeType.CRAFTING, craftingInput, serverLevel)
                .map(recipe -> {
                    final var resultStack = recipe.value().assemble(craftingInput);
                    if (!resultStack.isItemEnabled(serverLevel.enabledFeatures())) {
                        return null;
                    }
                    return new ProcessingResult(recipe, resultStack, createTicket(recipe, resultStack));
                });
    }

    private static Optional<ProcessingResult> computeAssemblyTicketResult(ServerLevel serverLevel, ArrayList<ItemStack> gridItems) {
        ItemStack ticketStack = ItemStack.EMPTY;
        for (final var gridItem : gridItems) {
            if (!gridItem.isEmpty()) {
                if (!ticketStack.isEmpty()) {
                    return Optional.empty();
                }
                ticketStack = gridItem;
            }
        }

        if (ticketStack.isEmpty() || !ticketStack.is(ModItems.assemblyTicket)) {
            return Optional.empty();
        }

        final var ticketData = ticketStack.get(ModDataComponents.assemblyTicket());
        if (ticketData == null || ticketData.recipeId().isEmpty()) {
            return Optional.empty();
        }

        final var recipe = serverLevel.recipeAccess()
                .byKey(ResourceKey.create(Registries.RECIPE, ticketData.recipeId().get()))
                .orElse(null);
        if (recipe == null) {
            return Optional.empty();
        }

        final var resultStack = getPreviewResult(serverLevel, recipe).orElse(ItemStack.EMPTY);
        if (!resultStack.isEmpty() && !resultStack.isItemEnabled(serverLevel.enabledFeatures())) {
            return Optional.empty();
        }

        final var ticketTitle = !resultStack.isEmpty() ? resultStack.getHoverName() : ticketStack.getHoverName();
        return Optional.of(new ProcessingResult(recipe, resultStack, createTicket(recipe, ticketTitle)));
    }

    private static Optional<ItemStack> getPreviewResult(Level level, RecipeHolder<?> recipe) {
        if (recipe.value() instanceof PreviewableRecipe previewableRecipe) {
            return Optional.of(previewableRecipe.previewResultItem());
        }

        final var context = SlotDisplayContext.fromLevel(level);
        return recipe.value().display().stream()
                .map(it -> it.result().resolveForFirstStack(context))
                .findFirst();
    }

    private static ItemStack createTicket(RecipeHolder<?> recipe, ItemStack resultStack) {
        return createTicket(recipe, resultStack.getHoverName());
    }

    private static ItemStack createTicket(RecipeHolder<?> recipe, Component title) {
        final var ticketStack = ModItems.assemblyTicket.createStack();
        ticketStack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.replikaentropie.assembly_ticket.tooltip.title", title));
        ticketStack.set(ModDataComponents.assemblyTicket(), new AssemblyTicket(recipe.id().identifier(), 1));
        return ticketStack;
    }

    private void serverTick(Level level) {
        final var match = computeResult(level, backingContainer).orElse(null);
        if (match == null || !hasSupplies() || !canOutput(match.ticketStack())) {
            processingTicks = 0;
            return;
        }

        processingTicks++;
        if (processingTicks < PROCESSING_TICKS) {
            return;
        }

        ContainerUtils.extractItem(backingContainer, PAPER_SLOT, 1, false);
        ContainerUtils.extractItem(backingContainer, INK_SLOT, 1, false);

        final var outputStack = backingContainer.getItem(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            backingContainer.setItem(OUTPUT_SLOT, match.ticketStack().copy());
        } else {
            outputStack.grow(match.ticketStack().getCount());
        }

        processingTicks = 0;
        setChanged();
    }

    private boolean hasSupplies() {
        return backingContainer.getItem(PAPER_SLOT).is(Items.PAPER)
                && !backingContainer.getItem(PAPER_SLOT).isEmpty()
                && backingContainer.getItem(INK_SLOT).is(Items.INK_SAC)
                && !backingContainer.getItem(INK_SLOT).isEmpty()
                && backingContainer.getItem(CYAN_DYE_SLOT).is(Items.CYAN_DYE)
                && !backingContainer.getItem(CYAN_DYE_SLOT).isEmpty();
    }

    private boolean canOutput(ItemStack resultStack) {
        final var outputStack = backingContainer.getItem(OUTPUT_SLOT);
        return outputStack.isEmpty()
                || ItemStack.isSameItemSameComponents(outputStack, resultStack)
                && outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        processingTicks = input.getIntOr("ProcessingTicks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putInt("ProcessingTicks", processingTicks);
    }

    public record ProcessingResult(RecipeHolder<?> recipe, ItemStack resultStack, ItemStack ticketStack) {
    }
}
