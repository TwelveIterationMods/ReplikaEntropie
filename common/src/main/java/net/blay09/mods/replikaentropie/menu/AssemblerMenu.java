package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.slot.AssemblerTicketSlot;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.blay09.mods.replikaentropie.recipe.AssemblerRecipe;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AssemblerMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final Container previewContainer = new SimpleContainer(5);
    private final Container container;
    private final ContainerData data;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME = 0;
    public static final int DATA_MAX_PROCESSING_TIME = 1;
    public static final int DATA_COUNT = 2;

    public AssemblerMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(7), new SimpleContainerData(DATA_COUNT));
    }

    public AssemblerMenu(int id, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.assembler.get(), id);
        this.playerInventory = playerInventory;
        this.container = container;
        checkContainerSize(container, 7);
        this.data = data;
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 80, 55));
        addSlot(new AssemblerTicketSlot(container, 1, 19, 55) {
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(container);
            }
        });

        for (int i = 0; i < 5; i++) {
            addSlot(new Slot(container, 2 + i, 44 + i * 18, 84));
        }

        for (int i = 0; i < 5; i++) {
            addSlot(new ReadonlySlot(previewContainer, i, 44 + i * 18, 26));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("ticket", 1)
                .slotRange("inputs", 2, 7)
                .route(it -> it.is(ModItems.assemblyTicket), QuickMove.PLAYER, "ticket")
                .route(it -> !it.is(ModItems.assemblyTicket), QuickMove.PLAYER, "inputs")
                .build();

        container.startOpen(playerInventory.player);

        updatePreviewFromTicket();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.container) {
            updatePreviewFromTicket();
        }
    }

    public float getAssemblyProgress() {
        final var processingTime = data.get(DATA_PROCESSING_TIME);
        final var maxProcessingTime = data.get(DATA_MAX_PROCESSING_TIME);
        return maxProcessingTime > 0 ? Mth.clamp(processingTime / (float) maxProcessingTime, 0f, 1f) : 0;
    }

    private void updatePreviewFromTicket() {
        final var ticketStack = container.getItem(1);
        if (ticketStack.isEmpty() || !ticketStack.hasTag()) {
            clearPreview();
            return;
        }

        final var itemData = ticketStack.getTag();
        final var recipeId = itemData != null ? ResourceLocation.tryParse(itemData.getString("ReplikaEntropieAssemblerResult")) : null;
        if (recipeId == null) {
            clearPreview();
            return;
        }

        final var recipeManager = playerInventory.player.level().getRecipeManager();
        final var recipe = recipeManager.byKey(recipeId).orElse(null);
        if (!(recipe instanceof AssemblerRecipe assemblerRecipe)) {
            clearPreview();
            return;
        }

        int i = 0;
        for (final var countedIngredient : assemblerRecipe.ingredients()) {
            if (i >= previewContainer.getContainerSize()) {
                break;
            }

            final var validItems = countedIngredient.ingredient().getItems();
            if (validItems.length > 0) {
                // POSTJAM Have preview slots support Ingredient
                final var itemStack = validItems[0].copy();
                itemStack.setCount(Math.max(1, countedIngredient.count()));
                previewContainer.setItem(i, itemStack);
            } else {
                previewContainer.setItem(i, ItemStack.EMPTY);
            }
            i++;
        }

        for (; i < previewContainer.getContainerSize(); i++) {
            previewContainer.setItem(i, ItemStack.EMPTY);
        }
    }

    private void clearPreview() {
        for (int i = 0; i < previewContainer.getContainerSize(); i++) {
            previewContainer.setItem(i, ItemStack.EMPTY);
        }
    }
}
