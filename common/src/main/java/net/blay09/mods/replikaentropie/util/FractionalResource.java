package net.blay09.mods.replikaentropie.util;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FractionalResource {

    private final Container container;
    private final int slot;
    private final Item item;

    private float fractionalAmount;

    public FractionalResource(Container container, int slot, Item item) {
        this.container = container;
        this.slot = slot;
        this.item = item;
    }

    public void add(float amount) {
        fractionalAmount += amount;
        produceItems();
    }

    public void setFractionalAmount(float fractionalAmount) {
        this.fractionalAmount = fractionalAmount;
        produceItems();
    }

    private void produceItems() {
        final var currentItemStack = container.getItem(slot);
        var space = item.getMaxStackSize();
        if (currentItemStack.is(item)) {
            space = Math.max(0, space - currentItemStack.getCount());
        } else if (!currentItemStack.isEmpty()) {
            space = 0;
        }

        final var countToAdd = Mth.clamp(Mth.floor(fractionalAmount), 0, space);
        fractionalAmount -= countToAdd;
        if (currentItemStack.isEmpty()) {
            container.setItem(slot, new ItemStack(item, countToAdd));
            setChanged();
        } else if (currentItemStack.is(item)) {
            currentItemStack.grow(countToAdd);
            setChanged();
        }
    }

    public float getFractionalAmount() {
        return fractionalAmount;
    }

    public int getFractionalAmountAsMenuData() {
        return (int) Math.floor(fractionalAmount * 100);
    }

    public boolean hasFractionalSpace() {
        return fractionalAmount < 1f;
    }

    public boolean hasSpace() {
        final var outputStack = container.getItem(slot);
        return outputStack.isEmpty()
                || (outputStack.is(item) && outputStack.getCount() < outputStack.getMaxStackSize());
    }

    protected void setChanged() {
    }
}
