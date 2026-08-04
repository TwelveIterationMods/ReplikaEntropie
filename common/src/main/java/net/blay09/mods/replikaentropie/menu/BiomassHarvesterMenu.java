package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
import net.blay09.mods.replikaentropie.core.harvester.BiomassHarvesterLogic;
import net.blay09.mods.replikaentropie.menu.slot.BiomassHarvesterToolSlot;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class BiomassHarvesterMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    public static final int DATA_CURRENT_POWER = 0;
    public static final int DATA_MAX_POWER = 1;
    public static final int DATA_OVERHEATED = 2;
    public static final int DATA_COUNT = 3;

    protected final Inventory inventory;
    protected final Container container;
    protected final ContainerData data;
    private final MakeshiftPsu makeshiftPsu;
    private final QuickMove.Routing quickMove;

    public BiomassHarvesterMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(BiomassHarvesterBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT), MakeshiftPsu.EMPTY);
    }

    public BiomassHarvesterMenu(int containerId, Inventory inventory, Container container, ContainerData data, MakeshiftPsu makeshiftPsu) {
        super(ModMenus.biomassHarvester.value(), containerId);
        this.inventory = inventory;
        this.container = container;
        checkContainerSize(container, BiomassHarvesterBlockEntity.CONTAINER_SIZE);
        this.data = data;
        this.makeshiftPsu = makeshiftPsu;
        addDataSlots(data);

        addSlot(new BiomassHarvesterToolSlot(container, 0, 78, 26));
        addSlot(new BiomassHarvesterToolSlot(container, 1, 108, 55));
        addSlot(new BiomassHarvesterToolSlot(container, 2, 78, 84));
        addSlot(new BiomassHarvesterToolSlot(container, 3, 49, 55));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slotRange("weapons", 0, 4)
                .route(BiomassHarvesterLogic::isValidHarvesterTool, QuickMove.PLAYER, "weapons")
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
