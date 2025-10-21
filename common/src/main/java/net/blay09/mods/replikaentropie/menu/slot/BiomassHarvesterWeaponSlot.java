package net.blay09.mods.replikaentropie.menu.slot;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

public class BiomassHarvesterWeaponSlot extends Slot {

    private final Pair<ResourceLocation, ResourceLocation> ICON = Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_sword_slot"));

    public BiomassHarvesterWeaponSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return BiomassHarvesterBlockEntity.isValidWeapon(itemStack);
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return ICON;
    }
}
