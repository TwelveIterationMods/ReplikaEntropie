package net.blay09.mods.replikaentropie.menu.slot;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class RecyclerSlot extends Slot {

    public static final List<Identifier> ICONS = List.of(
            id("item/empty_dust_slot"),
            id("item/empty_flesh_slot"),
            id("item/empty_ingot_slot"),
            id("item/empty_ore_slot")
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
