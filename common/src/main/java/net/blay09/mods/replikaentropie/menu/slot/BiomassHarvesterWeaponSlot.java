package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassHarvesterWeaponSlot extends Slot {

    private final Identifier ICON = id("item/empty_sword_slot");

    public BiomassHarvesterWeaponSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return BiomassHarvesterBlockEntity.isValidWeapon(itemStack);
    }

    @Override
    public Identifier getNoItemIcon() {
        return ICON;
    }
}
