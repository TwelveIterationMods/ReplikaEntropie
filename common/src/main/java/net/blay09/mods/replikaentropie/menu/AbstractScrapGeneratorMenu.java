package net.blay09.mods.replikaentropie.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScrapGeneratorMenu extends AbstractContainerMenu {

    protected final Inventory playerInventory;
    protected final Container container;
    protected final ContainerData data;

    public static final int DATA_PROCESSING_TICKS = 0;
    public static final int DATA_MAX_PROCESSING_TICKS = 1;
    public static final int DATA_COUNT = 3;

    protected AbstractScrapGeneratorMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
        this.container = container;
        this.data = data;
        addDataSlots(data);
    }

    protected void addPlayerInventorySlots(int y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, y + 85 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, y + 143));
        }
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

    public float getProcessingProgress() {
        final var progress = data.get(DATA_PROCESSING_TICKS);
        final var maxProgress = data.get(DATA_MAX_PROCESSING_TICKS);
        return maxProgress > 0 ? (float) progress / maxProgress : 0f;
    }

}
