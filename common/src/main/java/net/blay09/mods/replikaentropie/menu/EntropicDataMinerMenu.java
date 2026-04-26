package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.EntropicDataMinerBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EntropicDataMinerMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerLevelAccess access;
    private final QuickMove.Routing quickMove;

    public EntropicDataMinerMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(EntropicDataMinerBlockEntity.CONTAINER_SIZE), ContainerLevelAccess.NULL);
    }

    public EntropicDataMinerMenu(int containerId, Inventory inventory, Container container, ContainerLevelAccess access) {
        super(ModMenus.entropicDataMiner.value(), containerId);
        this.container = container;
        this.access = access;
        checkContainerSize(container, EntropicDataMinerBlockEntity.CONTAINER_SIZE);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                addSlot(new OutputSlot(container, j + i * 5, 75 + j * 18, 20 + i * 30));
            }
        }

        addStandardInventorySlots(inventory, 8, 84);

        quickMove = QuickMove.create(this, this::moveItemStackTo).build();

        container.startOpen(inventory.player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
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
