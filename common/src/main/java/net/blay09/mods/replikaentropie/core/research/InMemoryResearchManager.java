package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryResearchManager implements ResearchManager {

    private final Map<Identifier, ResearchState> researchStates = new HashMap<>();
    private final Map<Identifier, NonogramState> nonogramStates = new HashMap<>();

    @Override
    public ResearchState getResearchState(Player player, Identifier id) {
        return researchStates.getOrDefault(id, ResearchState.NONE);
    }

    @Override
    public Optional<NonogramState> getNonogramState(Player player, Identifier id) {
        return Optional.ofNullable(nonogramStates.get(id));
    }

    @Override
    public void updateResearch(Player player, Identifier id, ResearchState state) {
        researchStates.put(id, state);
    }

    @Override
    public void updateNonogram(Player player, Identifier id, NonogramState state) {
        nonogramStates.put(id, state);
    }

    @Override
    public void resetAllResearch(Player player) {
        researchStates.clear();
    }
}
