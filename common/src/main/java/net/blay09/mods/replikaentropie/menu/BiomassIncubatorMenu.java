package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.BiomassIncubatorBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.IngredientSlot;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class BiomassIncubatorMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    public static final int DATA_WATER_TANK = 0;
    public static final int DATA_MAX_WATER_TANK = 1;
    public static final int DATA_GROWTH_TIME = 2;
    public static final int DATA_MAX_GROWTH_TIME = 3;
    public static final int DATA_CURRENT_POWER = 4;
    public static final int DATA_MAX_POWER = 5;
    public static final int DATA_OVERHEATED = 6;
    public static final int DATA_COUNT = 7;

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private final MakeshiftPsu makeshiftPsu;

    private final QuickMove.Routing quickMove;

    public BiomassIncubatorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(BiomassIncubatorBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, MakeshiftPsu.EMPTY);
    }

    public BiomassIncubatorMenu(int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, MakeshiftPsu makeshiftPsu) {
        super(ModMenus.biomassIncubator.value(), containerId);
        this.inventory = inventory;
        this.container = container;
        checkContainerSize(container, BiomassIncubatorBlockEntity.CONTAINER_SIZE);
        this.data = data;
        this.makeshiftPsu = makeshiftPsu;
        addDataSlots(data);

        addSlot(new IngredientSlot(container, 0, 45, 80, Ingredient.of(Items.WATER_BUCKET)));

        addSlot(new Slot(container, 1, 80, 37));
        addSlot(new Slot(container, 2, 80, 63));

        addSlot(new Slot(container, 3, 115, 26));
        addSlot(new Slot(container, 4, 115, 44));
        addSlot(new Slot(container, 5, 115, 62));
        addSlot(new Slot(container, 6, 115, 80));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 121 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 179));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("water", 0)
                .slot("seeds", 1)
                .slot("soil", 2)
                .route(it -> it.is(Items.WATER_BUCKET), QuickMove.PLAYER, "water")
                .route(it -> it.is(ModItemTags.BIOMASS_INCUBATOR_SEEDS), QuickMove.PLAYER, "seeds")
                .route(it -> it.is(ModItemTags.BIOMASS_INCUBATOR_SOILS), QuickMove.PLAYER, "soil")
                .build();

        container.startOpen(inventory.player);
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

    public float getPowerProgress() {
        final var maxPower = data.get(DATA_MAX_POWER);
        if (maxPower <= 0) {
            return 0f;
        }

        return Mth.clamp(data.get(DATA_CURRENT_POWER) / (float) maxPower, 0f, 1f);
    }

    public int getCurrentPower() {
        return data.get(DATA_CURRENT_POWER);
    }

    public int getMaxPower() {
        return data.get(DATA_MAX_POWER);
    }

    @Override
    public boolean isMakeshiftPsuOverheated() {
        return data.get(DATA_OVERHEATED) != 0;
    }

    @Override
    public MakeshiftPsu getMakeshiftPsu() {
        return makeshiftPsu;
    }

}
