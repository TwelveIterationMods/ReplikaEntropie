package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReplikaWorkbenchSlot extends Slot {
    public ReplikaWorkbenchSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.is(ModItemTags.REPLIKA_FRAME);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
