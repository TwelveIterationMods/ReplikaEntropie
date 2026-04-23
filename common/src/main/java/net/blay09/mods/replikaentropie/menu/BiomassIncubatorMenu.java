package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.menu.slot.IngredientSlot;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.blay09.mods.replikaentropie.recipe.BiomassIncubatorRecipe;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class BiomassIncubatorMenu extends AbstractContainerMenu {

    public static final int DATA_WATER_TANK = 0;
    public static final int DATA_MAX_WATER_TANK = 1;
    public static final int DATA_GROWTH_TIME = 2;
    public static final int DATA_MAX_GROWTH_TIME = 3;
    public static final int DATA_FRACTIONAL_BIOMASS = 4;
    public static final int DATA_COUNT = 5;

    private final Container container;
    private final ContainerData data;

    private final QuickMove.Routing quickMove;

    public BiomassIncubatorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(8), new SimpleContainerData(DATA_COUNT));
    }

    public BiomassIncubatorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.biomassIncubator.value(), containerId);
        this.container = container;
        checkContainerSize(container, 8);
        this.data = data;
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 133, 53));

        addSlot(new IngredientSlot(container, 1, 24, 80, Ingredient.of(Items.WATER_BUCKET)));

        addSlot(new Slot(container, 2, 59, 62));
        addSlot(new Slot(container, 3, 59, 37));

        addSlot(new Slot(container, 4, 94, 26));
        addSlot(new Slot(container, 5, 94, 44));
        addSlot(new Slot(container, 6, 94, 62));
        addSlot(new Slot(container, 7, 94, 80));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 121 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 179));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("water", 1)
                .slot("soil", 2)
                .slot("seeds", 3)
                .route(it -> it.is(Items.WATER_BUCKET), QuickMove.PLAYER, "water")
                .route(it -> BiomassIncubatorRecipe.getRecipe(playerInventory.player.level(), it).isPresent(), QuickMove.PLAYER, "seeds")
                .build();

        container.startOpen(playerInventory.player);
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

    public int getWaterTank() {
        return data.get(DATA_WATER_TANK);
    }

    public int getMaxWaterCapacity() {
        return data.get(DATA_MAX_WATER_TANK);
    }

    public float getWaterTankProgress() {
        return getMaxWaterCapacity() > 0 ? (float) getWaterTank() / getMaxWaterCapacity() : 0f;
    }

    public int getGrowthTime() {
        return data.get(DATA_GROWTH_TIME);
    }

    public int getMaxGrowthTime() {
        return data.get(DATA_MAX_GROWTH_TIME);
    }

    public float getGrowthProgress() {
        return getMaxGrowthTime() > 0 ? (float) getGrowthTime() / getMaxGrowthTime() : 0f;
    }

    public float getFractionalBiomass() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_BIOMASS) / 100f, 0f, 1f);
    }

}
