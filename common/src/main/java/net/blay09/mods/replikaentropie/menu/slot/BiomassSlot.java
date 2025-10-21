package net.blay09.mods.replikaentropie.menu.slot;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.crafting.Ingredient;

public class BiomassSlot extends IngredientSlot {

    private final Pair<ResourceLocation, ResourceLocation> ICON = Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_biomass_slot"));

    public BiomassSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y, Ingredient.of(ModItems.biomass));
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return ICON;
    }
}
