package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.EntropicDataMinerBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class EntropicDataMinerMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    public static final int DATA_CURRENT_POWER = 0;
    public static final int DATA_MAX_POWER = 1;
    public static final int DATA_COUNT = 2;

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final QuickMove.Routing quickMove;

    public EntropicDataMinerMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(EntropicDataMinerBlockEntity.EVENT_HISTORY_SIZE), new SimpleContainer(EntropicDataMinerBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public EntropicDataMinerMenu(int containerId, Inventory inventory, Container eventHistoryContainer, Container container, ContainerData data, ContainerLevelAccess access) {
        this(ModMenus.entropicDataMiner.value(), containerId, inventory, eventHistoryContainer, container, data, access);
    }

    public EntropicDataMinerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, Container eventHistoryContainer, Container container, ContainerData data, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.inventory = inventory;
        this.container = container;
        this.data = data;
        this.access = access;
        checkContainerSize(container, EntropicDataMinerBlockEntity.CONTAINER_SIZE);
        checkContainerSize(eventHistoryContainer, EntropicDataMinerBlockEntity.EVENT_HISTORY_SIZE);
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 30, 35));

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 4; column++) {
                addSlot(new ReadonlySlot(eventHistoryContainer, column + row * 4, 79 + column * 21, 24 + row * 21));
            }
        }

        addStandardInventorySlots(inventory, 8, 84);

        quickMove = QuickMove.create(this, this::moveItemStackTo).build();

        container.startOpen(inventory.player);
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
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
    }

    @Override
    public void convertClickToPower() {
        access.execute((level, pos) -> {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EntropicDataMinerBlockEntity dataMiner) {
                dataMiner.getEnergyStorage().fill(inventory.player.isCreative() ? Integer.MAX_VALUE : 250, false);
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).is(ModBlocks.entropicDataMiner) && player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5F, pos.getZ() + 0.5f) <= 64f, true);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
