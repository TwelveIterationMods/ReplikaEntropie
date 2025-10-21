package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.BiomassHarvesterWeaponSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class BiomassHarvesterMenu extends AbstractContainerMenu {

    public static final int DATA_FRACTIONAL_BIOMASS = 0;
    public static final int DATA_COUNT = 1;

    protected final Inventory playerInventory;
    protected final Container container;
    protected final ContainerData data;
    private final QuickMove.Routing quickMove;

    public BiomassHarvesterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(5), new SimpleContainerData(DATA_COUNT));
    }

    public BiomassHarvesterMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.biomassHarvester.get(), containerId);
        this.playerInventory = playerInventory;
        this.container = container;
        checkContainerSize(container, 5);
        this.data = data;
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 78, 55));

        addSlot(new BiomassHarvesterWeaponSlot(container, 1, 78, 26));
        addSlot(new BiomassHarvesterWeaponSlot(container, 2, 108, 55));
        addSlot(new BiomassHarvesterWeaponSlot(container, 3, 78, 84));
        addSlot(new BiomassHarvesterWeaponSlot(container, 4, 49, 55));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slotRange("weapons", 1, 5)
                .route(BiomassHarvesterBlockEntity::isValidWeapon, QuickMove.PLAYER, "weapons")
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

    public float getFractionalBiomass() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_BIOMASS) / 100f, 0f, 1f);
    }
}
