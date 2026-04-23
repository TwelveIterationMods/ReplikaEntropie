package net.blay09.mods.replikaentropie.menu.slot;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class RecyclerSlot extends Slot {

    public static final List<Identifier> ICONS = List.of(
            id("container/slot/dust"),
            id("container/slot/flesh"),
            id("container/slot/ingot"),
            id("container/slot/ore")
    );

    private long lastIconChange;
    private int currentIconIndex;

    public RecyclerSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public Identifier getNoItemIcon() {
        final var now = System.currentTimeMillis();
        final var timePassed = now - lastIconChange;
        if (timePassed >= 1500) {
            currentIconIndex = (currentIconIndex + 1) % ICONS.size();
            lastIconChange = now;
        }
        return ICONS.get(currentIconIndex);
    }
}
