package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramClueProvider;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.core.research.ResearchManagers;
import net.blay09.mods.replikaentropie.core.research.ResearchState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class NonogramResearchMenu extends NonogramMenu {

    private final Identifier researchId;

    public NonogramResearchMenu(int containerId, Inventory inventory, NonogramClueProvider clues, NonogramState nonogramState, Identifier researchId) {
        super(containerId, inventory, clues, nonogramState);
        this.researchId = researchId;
    }

    @Override
    public void mark(int column, int row, int mark) {
        super.mark(column, row, mark);
        ResearchManagers.updateNonogram(playerInventory.player, researchId, nonogramState);
    }

    @Override
    public void markCompleted() {
        super.markCompleted();
        ResearchManagers.updateResearch(playerInventory.player, researchId, ResearchState.UNLOCKED);
    }
}
