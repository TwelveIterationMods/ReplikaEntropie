package net.blay09.mods.replikaentropie.menu.slot;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.crafting.Ingredient;

public class AssemblerTicketSlot extends IngredientSlot {

    private final Pair<ResourceLocation, ResourceLocation> ICON = Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_assembly_ticket_slot"));

    public AssemblerTicketSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y, Ingredient.of(ModItems.assemblyTicket));
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return ICON;
    }
}
