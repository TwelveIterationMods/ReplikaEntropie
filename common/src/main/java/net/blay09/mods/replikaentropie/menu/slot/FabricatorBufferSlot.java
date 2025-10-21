package net.blay09.mods.replikaentropie.menu.slot;

import net.minecraft.world.Container;

public class FabricatorBufferSlot extends ReadonlySlot {
    public FabricatorBufferSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean isHighlightable() {
        return false;
    }
}
