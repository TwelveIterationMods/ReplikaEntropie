package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramClues;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractNonogramMenu extends AbstractContainerMenu {

    protected AbstractNonogramMenu(MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        final var column = id & 0xFF;
        final var row = (id >> 8) & 0xFF;
        final var mark = (byte) ((id >> 16) & 0xFF);
        mark(column, row, mark);
        return true;
    }

    public abstract NonogramClues getClues();

    public abstract NonogramState getNonogramState();

    public abstract NonogramClues.ErrorState getErrors();

    public abstract void mark(int column, int row, int mark);

    public boolean isCompleted() {
        return false;
    }
}

