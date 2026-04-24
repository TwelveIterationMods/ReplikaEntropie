package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassHarvesterToolSlot extends Slot {

    public static final List<Identifier> ICONS = List.of(
            id("container/slot/sword"),
            id("container/slot/hoe"),
            id("container/slot/shears")
    );

    private long lastIconChange;
    private int currentIconIndex;

    public BiomassHarvesterToolSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return BiomassHarvesterBlockEntity.isValidHarvesterTool(itemStack);
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
