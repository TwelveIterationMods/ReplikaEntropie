package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.BluePrinterBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.IngredientSlot;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BluePrinterMenu extends AbstractContainerMenu {

    private final Inventory inventory;
    private final Container container;
    private final Container previewContainer = new SimpleContainer(1);
    private final QuickMove.Routing quickMove;

    public BluePrinterMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(BluePrinterBlockEntity.CONTAINER_SIZE), ContainerLevelAccess.NULL);
    }

    public BluePrinterMenu(int id, Inventory inventory, Container container, ContainerLevelAccess access) {
        super(ModMenus.bluePrinter.value(), id);
        this.inventory = inventory;
        this.container = container;
        checkContainerSize(container, BluePrinterBlockEntity.CONTAINER_SIZE);

        addSlot(new IngredientSlot(container, BluePrinterBlockEntity.PAPER_SLOT, 81, 50, Ingredient.of(Items.PAPER), id("container/slot/paper")));
        addSlot(new IngredientSlot(container, BluePrinterBlockEntity.INK_SLOT, 81, 25, Ingredient.of(Items.INK_SAC), id("container/slot/ink_sac")));
        addSlot(new IngredientSlot(container, BluePrinterBlockEntity.CYAN_DYE_SLOT, 106, 25, Ingredient.of(Items.CYAN_DYE), id("container/slot/cyan_dye")));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                final int slot = BluePrinterBlockEntity.GRID_START + column + row * 3;
                addSlot(new Slot(container, slot, 17 + column * 18, 21 + row * 18) {
                    @Override
                    public void setChanged() {
                        slotsChanged(container);
                    }
                });
            }
        }

        addSlot(new OutputSlot(container, BluePrinterBlockEntity.OUTPUT_SLOT, 139, 50));
        addSlot(new ReadonlySlot(previewContainer, 0, 139, 25));

        addStandardInventorySlots(inventory, 8, 92);

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("paper", BluePrinterBlockEntity.PAPER_SLOT)
                .slot("ink", BluePrinterBlockEntity.INK_SLOT)
                .slot("cyan_dye", BluePrinterBlockEntity.CYAN_DYE_SLOT)
                .slotRange("grid", BluePrinterBlockEntity.GRID_START, BluePrinterBlockEntity.OUTPUT_SLOT)
                .route(it -> it.is(Items.PAPER), QuickMove.PLAYER, "paper")
                .route(it -> it.is(Items.INK_SAC), QuickMove.PLAYER, "ink")
                .route(it -> it.is(Items.CYAN_DYE), QuickMove.PLAYER, "cyan_dye")
                .route(QuickMove.PLAYER, "grid")
                .build();

        container.startOpen(inventory.player);
        updatePreview();
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
            updatePreview();
        }
    }

    private void updatePreview() {
        if (!(inventory.player.level() instanceof ServerLevel serverLevel)) {
            clearPreview();
            return;
        }

        previewContainer.setItem(0, BluePrinterBlockEntity.computeResult(serverLevel, container)
                .map(BluePrinterBlockEntity.ProcessingResult::resultStack)
                .orElse(ItemStack.EMPTY));
    }

    private void clearPreview() {
        previewContainer.setItem(0, ItemStack.EMPTY);
    }
}
