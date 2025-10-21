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

public class FragmentAcceleratorMenu extends AbstractContainerMenu {

    public static final int DATA_PROCESSING_TIME = 0;
    public static final int DATA_MAX_PROCESSING_TIME = 1;
    public static final int DATA_FRACTIONAL_FRAGMENTS = 2;
    public static final int DATA_COUNT = 3;

    protected final Inventory playerInventory;
    protected final Container container;
    protected final ContainerData data;
    private final QuickMove.Routing quickMove;

    public FragmentAcceleratorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(8), new SimpleContainerData(DATA_COUNT));
    }

    public FragmentAcceleratorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.fragmentAccelerator.get(), containerId);
        this.playerInventory = playerInventory;
        this.container = container;
        checkContainerSize(container, 8);
        this.data = data;
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 61, 55));
        addSlot(new OutputSlot(container, 1, 92, 55));

        addSlot(new Slot(container, 2, 61, 26));
        addSlot(new Slot(container, 3, 92, 26));
        addSlot(new Slot(container, 4, 121, 55));
        addSlot(new Slot(container, 5, 92, 84));
        addSlot(new Slot(container, 6, 61, 84));
        addSlot(new Slot(container, 7, 32, 55));


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slotRange("inputs", 2, 8)
                .route(QuickMove.PLAYER, "inputs")
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

    public float getProcessingProgress() {
        int progress = data.get(DATA_PROCESSING_TIME);
        int maxProgress = data.get(DATA_MAX_PROCESSING_TIME);
        return maxProgress > 0 ? (float) progress / maxProgress : 0f;
    }

    public float getFractionalFragments() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_FRAGMENTS) / 100f, 0, 1f);
    }
}
