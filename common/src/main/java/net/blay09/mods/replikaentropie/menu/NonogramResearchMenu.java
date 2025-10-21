package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramClueProvider;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.core.research.Research;
import net.blay09.mods.replikaentropie.core.research.ResearchState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NonogramResearchMenu extends NonogramMenu {

    private final ResourceLocation researchId;

    public NonogramResearchMenu(int containerId, Inventory inventory, NonogramClueProvider clues, NonogramState nonogramState, ResourceLocation researchId) {
        super(containerId, inventory, clues, nonogramState);
        this.researchId = researchId;
    }

    @Override
    public void mark(int column, int row, int mark) {
        super.mark(column, row, mark);
        Research.updateNonogram(playerInventory.player, researchId, nonogramState);
    }

    @Override
    public void markCompleted() {
        super.markCompleted();
        Research.updateResearch(playerInventory.player, researchId, ResearchState.UNLOCKED);
    }
}
