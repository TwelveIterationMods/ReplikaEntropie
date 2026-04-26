package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.ReplikaWorkbenchBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.ReplikaWorkbenchSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ReplikaWorkbenchMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    public static final int DATA_CURRENT_POWER = 0;
    public static final int DATA_MAX_POWER = 1;
    public static final int DATA_COUNT = 2;

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final QuickMove.Routing quickMove;

    public ReplikaWorkbenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(9), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public ReplikaWorkbenchMenu(int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        this(ModMenus.replikaWorkbench.value(), containerId, inventory, container, data, access);
    }

    public ReplikaWorkbenchMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.inventory = inventory;
        this.container = container;
        this.data = data;
        this.access = access;
        addDataSlots(data);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final var index = j * 3 + i;
                final var x = 22 + i * 29;
                final var y = 26 + j * 29;
                if (index == 4) {
                    addSlot(new ReplikaWorkbenchSlot(container, index, x, y));
                } else {
                    addSlot(new Slot(container, index, x, y));
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slotRange("parts1", 0, 4)
                .slotRange("parts2", 5, 10)
                .slot("center", 4)
                .build();

        container.startOpen(inventory.player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public float getPowerProgress() {
        final var maxPower = data.get(DATA_MAX_POWER);
        if (maxPower <= 0) {
            return 0f;
        }

        return Mth.clamp(data.get(DATA_CURRENT_POWER) / (float) maxPower, 0f, 1f);
    }

    @Override
    public void convertClickToPower() {
        access.execute((level, pos) -> {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ReplikaWorkbenchBlockEntity replikaWorkbench) {
                replikaWorkbench.getEnergyStorage().fill(inventory.player.isCreative() ? Integer.MAX_VALUE : 250, false);
            }
        });
    }
}
