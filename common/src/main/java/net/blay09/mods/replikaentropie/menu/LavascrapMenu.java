package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.LavascrapBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.IngredientSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class LavascrapMenu extends AbstractScrapGeneratorMenu {

    public static final int DATA_WATER_TANK = AbstractScrapGeneratorMenu.DATA_COUNT;
    public static final int DATA_MAX_WATER_TANK = AbstractScrapGeneratorMenu.DATA_COUNT + 1;
    public static final int DATA_LAVA_TANK = AbstractScrapGeneratorMenu.DATA_COUNT + 2;
    public static final int DATA_MAX_LAVA_TANK = AbstractScrapGeneratorMenu.DATA_COUNT + 3;
    public static final int DATA_COUNT = AbstractScrapGeneratorMenu.DATA_COUNT + 4;

    private final QuickMove.Routing quickMove;

    public LavascrapMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(LavascrapBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public LavascrapMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.lavascrap.value(), containerId, playerInventory, container, data);

        checkContainerSize(container, LavascrapBlockEntity.CONTAINER_SIZE);

        addSlot(new OutputSlot(container, 0, 80, 38));
        addSlot(new IngredientSlot(container, 1, 17, 79, Ingredient.of(Items.WATER_BUCKET)));
        addSlot(new IngredientSlot(container, 2, 143, 79, Ingredient.of(Items.LAVA_BUCKET)));

        addPlayerInventorySlots(31);

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("water", 1)
                .slot("lava", 2)
                .route(it -> it.is(Items.WATER_BUCKET), QuickMove.PLAYER, "water")
                .route(it -> it.is(Items.LAVA_BUCKET), QuickMove.PLAYER, "lava")
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

    public int getWaterTank() {
        return data.get(DATA_WATER_TANK);
    }

    public int getLavaTank() {
        return data.get(DATA_LAVA_TANK);
    }

    public int getMaxWaterCapacity() {
        return data.get(DATA_MAX_WATER_TANK);
    }

    public int getMaxLavaCapacity() {
        return data.get(DATA_MAX_LAVA_TANK);
    }

    public float getWaterTankProgress() {
        return getMaxWaterCapacity() > 0 ? (float) getWaterTank() / getMaxWaterCapacity() : 0f;
    }

    public float getLavaTankProgress() {
        return getMaxLavaCapacity() > 0 ? (float) getLavaTank() / getMaxLavaCapacity() : 0f;
    }
}
