package net.blay09.mods.replikaentropie.menu.slot;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;

public class IngredientSlot extends Slot {
    private final Ingredient ingredient;
    private final @Nullable Identifier noIconTexture;

    public IngredientSlot(Container container, int slot, int x, int y, Ingredient ingredient) {
        this(container, slot, x, y, ingredient, null);
    }

    public IngredientSlot(Container container, int slot, int x, int y, Ingredient ingredient, @Nullable Identifier noIconTexture) {
        super(container, slot, x, y);
        this.ingredient = ingredient;
        this.noIconTexture = noIconTexture;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return ingredient.test(stack);
    }

    @Override
    public @Nullable Identifier getNoItemIcon() {
        return noIconTexture;
    }
}
