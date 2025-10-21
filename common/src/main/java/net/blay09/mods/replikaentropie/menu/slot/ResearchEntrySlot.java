package net.blay09.mods.replikaentropie.menu.slot;

import net.blay09.mods.replikaentropie.container.SingleItemContainer;
import net.blay09.mods.replikaentropie.menu.ResearchMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ResearchEntrySlot extends ReadonlySlot {
    @Nullable
    private ResearchMenu.StatefulResearchEntry researchEntry;

    private ItemStack icon = ItemStack.EMPTY;

    public ResearchEntrySlot(int x, int y) {
        super(new SingleItemContainer(), 0, x, y);
    }

    public void setResearchEntry(@Nullable ResearchMenu.StatefulResearchEntry researchEntry) {
        this.researchEntry = researchEntry;
        icon = researchEntry != null ? researchEntry.recipe().icon().copy() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem() {
        return icon;
    }

    @Nullable
    public ResearchMenu.StatefulResearchEntry getResearchEntry() {
        return researchEntry;
    }

    @Override
    public boolean isHighlightable() {
        return researchEntry != null;
    }
}

