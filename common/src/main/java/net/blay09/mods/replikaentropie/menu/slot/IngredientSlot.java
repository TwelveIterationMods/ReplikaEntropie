package net.blay09.mods.replikaentropie.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientSlot extends Slot {
    private final Ingredient ingredient;

    public IngredientSlot(Container container, int slot, int x, int y, Ingredient ingredient) {
        super(container, slot, x, y);
        this.ingredient = ingredient;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return ingredient.test(stack);
    }
}
