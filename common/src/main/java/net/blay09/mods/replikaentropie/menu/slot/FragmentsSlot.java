package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FragmentsSlot extends IngredientSlot {

    private final Identifier ICON = id("container/slot/fragments");

    public FragmentsSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y, Ingredient.of(ModItems.fragments));
    }

    @Override
    public Identifier getNoItemIcon() {
        return ICON;
    }
}
