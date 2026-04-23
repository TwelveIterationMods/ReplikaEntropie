package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassSlot extends IngredientSlot {

    private final Identifier ICON = id("container/slot/biomass");

    public BiomassSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y, Ingredient.of(ModItems.biomass));
    }

    @Override
    public Identifier getNoItemIcon() {
        return ICON;
    }
}
