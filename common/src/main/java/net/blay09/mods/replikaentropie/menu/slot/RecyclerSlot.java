package net.blay09.mods.replikaentropie.menu.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecyclerSlot extends Slot {

    public static final List<Pair<ResourceLocation, ResourceLocation>> ICONS = List.of(
            Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_dust_slot")),
            Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_flesh_slot")),
            Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_ingot_slot")),
            Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("replikaentropie", "item/empty_ore_slot"))
    );

    private long lastIconChange;
    private int currentIconIndex;

    public RecyclerSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        final var now = System.currentTimeMillis();
        final var timePassed = now - lastIconChange;
        if (timePassed >= 1500) {
            currentIconIndex = (currentIconIndex + 1) % ICONS.size();
            lastIconChange = now;
        }
        return ICONS.get(currentIconIndex);
    }
}
