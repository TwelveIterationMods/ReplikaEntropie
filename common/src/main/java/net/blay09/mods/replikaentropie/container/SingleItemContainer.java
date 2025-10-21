package net.blay09.mods.replikaentropie.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ticks.ContainerSingleItem;

public class SingleItemContainer implements ContainerSingleItem {

    private ItemStack itemStack = ItemStack.EMPTY;

    public SingleItemContainer() {
    }

    public SingleItemContainer(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? itemStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0) {
            return itemStack.split(amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (slot == 0) {
            this.itemStack = itemStack;
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
