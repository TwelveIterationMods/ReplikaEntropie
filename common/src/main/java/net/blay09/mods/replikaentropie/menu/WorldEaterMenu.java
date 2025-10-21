package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class WorldEaterMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    private final QuickMove.Routing quickMove;

    public static final int DATA_SCANNING_TIME = 0;
    public static final int DATA_MAX_SCANNING_TIME = 1;
    public static final int DATA_DESTROYING_TIME = 2;
    public static final int DATA_MAX_DESTROYING_TIME = 3;
    public static final int DATA_CURRENT_DESTROY_SLOT = 4;
    public static final int DATA_FRACTIONAL_SCRAP = 5;
    public static final int DATA_COUNT = 6;

    public WorldEaterMenu(int containerId, Inventory playerInventory, Container previewContainer, Container container, ContainerData data) {
        super(ModMenus.worldEater.get(), containerId);
        this.container = container;
        checkContainerSize(container, 1);
        checkContainerSize(previewContainer, 15);
        this.data = data;
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 134, 60));

        for (int j = 0; j < 5; j++) {
            addSlot(new ReadonlySlot(previewContainer, j, 22 + j * 18, 24));
        }
        for (int j = 0; j < 5; j++) {
            addSlot(new ReadonlySlot(previewContainer, 5 + j, 22 + 72 - j * 18, 24 + 18));
        }
        for (int j = 0; j < 5; j++) {
            addSlot(new ReadonlySlot(previewContainer, 10 + j, 22 + j * 18, 24 + 18 * 2));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 98 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 156));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot(QuickMove.CONTAINER, 0)
                .build();

        container.startOpen(playerInventory.player);
    }

    public WorldEaterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(15), new SimpleContainer(1), new SimpleContainerData(DATA_COUNT));
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

    public float getScanningProgress() {
        int progress = data.get(DATA_SCANNING_TIME);
        int maxProgress = data.get(DATA_MAX_SCANNING_TIME);
        return maxProgress > 0 ? (float) progress / maxProgress : 0f;
    }

    public float getDestroyingProgress() {
        int progress = data.get(DATA_DESTROYING_TIME);
        int maxProgress = data.get(DATA_MAX_DESTROYING_TIME);
        return maxProgress > 0 ? (float) progress / maxProgress : 0f;
    }

    public boolean isScanning() {
        return data.get(DATA_SCANNING_TIME) > 0;
    }

    public boolean isDestroying() {
        return data.get(DATA_CURRENT_DESTROY_SLOT) >= 0;
    }

    public int getCurrentDestroySlot() {
        return data.get(DATA_CURRENT_DESTROY_SLOT);
    }

    public float getFractionalScrap() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_SCRAP) / 100f, 0, 1f);
    }

}
