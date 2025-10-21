package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryResearchManager implements ResearchManager {

    private final Map<ResourceLocation, ResearchState> researchStates = new HashMap<>();
    private final Map<ResourceLocation, NonogramState> nonogramStates = new HashMap<>();

    @Override
    public ResearchState getResearchState(Player player, ResourceLocation id) {
        return researchStates.getOrDefault(id, ResearchState.NONE);
    }

    @Override
    public Optional<NonogramState> getNonogramState(Player player, ResourceLocation id) {
        return Optional.ofNullable(nonogramStates.get(id));
    }

    @Override
    public void updateResearch(Player player, ResourceLocation id, ResearchState state) {
        researchStates.put(id, state);
    }

    @Override
    public void updateNonogram(Player player, ResourceLocation id, NonogramState state) {
        nonogramStates.put(id, state);
    }

    @Override
    public void resetAllResearch(Player player) {
        researchStates.clear();
    }
}
