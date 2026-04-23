package net.blay09.mods.replikaentropie.menu.slot;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FragmentAcceleratorWasteSlot extends Slot {

    public static final Identifier ICON = id("container/slot/waste_barrel");

    public FragmentAcceleratorWasteSlot(Container container, int i, int x, int y) {
        super(container, i, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public Identifier getNoItemIcon() {
        return ICON;
    }
}
