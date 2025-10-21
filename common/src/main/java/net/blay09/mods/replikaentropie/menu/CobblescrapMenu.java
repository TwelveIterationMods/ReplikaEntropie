package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.menu.slot.IngredientSlot;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class CobblescrapMenu extends AbstractScrapGeneratorMenu {

    private final QuickMove.Routing quickMove;

    public CobblescrapMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), new SimpleContainerData(DATA_COUNT));
    }

    public CobblescrapMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.cobblescrap.get(), containerId, playerInventory, container, data);

        checkContainerSize(container, 2);

        addSlot(new OutputSlot(container, 0, 80, 80));
        addSlot(new IngredientSlot(container, 1, 80, 22, Ingredient.of(Items.COBBLESTONE)));

        addPlayerInventorySlots();

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("input", 1)
                .route(it -> it.is(Items.COBBLESTONE), QuickMove.PLAYER, "input")
                .build();

        container.startOpen(playerInventory.player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
