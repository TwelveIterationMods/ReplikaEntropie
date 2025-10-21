package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class DefragmentizerMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME_START = 0;
    public static final int DATA_PROCESSING_TIME_END = 3;
    public static final int DATA_MAX_PROCESSING_TIME_START = 4;
    public static final int DATA_MAX_PROCESSING_TIME_END = 7;
    public static final int DATA_FRACTIONAL_FRAGMENTS_START = 8;
    public static final int DATA_FRACTIONAL_FRAGMENTS_END = 11;
    public static final int DATA_COUNT = 12;

    public DefragmentizerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(8), new SimpleContainerData(DATA_COUNT));
    }

    public DefragmentizerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.defragmentizer.get(), containerId);
        this.container = container;
        checkContainerSize(container, 8);
        this.data = data;
        addDataSlots(data);

        for (int i = 0; i < 4; i++) {
            addSlot(new Slot(container, i, 22 + i * 37, 22));
        }

        for (int i = 0; i < 4; i++) {
            addSlot(new OutputSlot(container, i + 4, 22 + i * 37, 80));
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
                .slotRange("inputs", 0, 4)
                .route(QuickMove.PLAYER, "inputs")
                .build();

        container.startOpen(playerInventory.player);
    }

    public float getProcessingProgress(int slot) {
        final var processingTime = data.get(DATA_PROCESSING_TIME_START + slot);
        final var maxProcessingTime = data.get(DATA_MAX_PROCESSING_TIME_START + slot);
        return maxProcessingTime == 0 ? 0f : (float) processingTime / maxProcessingTime;
    }

    public float getFractionalFragments(int slot) {
        return Mth.clamp(data.get(DATA_FRACTIONAL_FRAGMENTS_START + slot) / 100f, 0f, 1f);
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
}
