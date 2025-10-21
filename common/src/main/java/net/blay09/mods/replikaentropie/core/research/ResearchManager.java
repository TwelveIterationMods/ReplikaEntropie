package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface ResearchManager {
    ResearchState getResearchState(Player player, ResourceLocation id);

    Optional<NonogramState> getNonogramState(Player player, ResourceLocation id);

    void updateResearch(Player player, ResourceLocation id, ResearchState state);

    void updateNonogram(Player player, ResourceLocation id, NonogramState state);

    void resetAllResearch(Player player);

    default boolean meetsDependencies(Player player, @Nullable List<ResourceLocation> dependencies) {
        if (dependencies == null) {
            return true;
        }
        return dependencies.stream().noneMatch(dependency -> getResearchState(player, dependency) != ResearchState.UNLOCKED);
    }
}
