package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.container.SingleItemContainer;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.world.item.ItemStack;

public class ResearchCostSlot extends ReadonlySlot {

    public enum Type {
        DATA,
        SCRAP,
        BIOMASS,
        FRAGMENTS
    }

    private final Type type;

    private ItemStack clientStack = ItemStack.EMPTY;

    private int cost;
    private int available;

    public ResearchCostSlot(int x, int y, Type type) {
        super(new SingleItemContainer(), 0, x, y);
        this.type = type;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
        if (cost > 0) {
            clientStack = new ItemStack(switch (type) {
                case DATA -> ModItems.data;
                case SCRAP -> ModItems.scrap;
                case BIOMASS -> ModItems.biomass;
                case FRAGMENTS -> ModItems.fragments;
            }, cost);
        } else {
            clientStack = ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack getItem() {
        return clientStack;
    }

    @Override
    public boolean isHighlightable() {
        return cost > 0;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getAvailable() {
        return available;
    }
}
