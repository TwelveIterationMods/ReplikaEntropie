package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface ResearchManager {
    ResearchState getResearchState(Player player, Identifier id);

    Optional<NonogramState> getNonogramState(Player player, Identifier id);

    void updateResearch(Player player, Identifier id, ResearchState state);

    void updateNonogram(Player player, Identifier id, NonogramState state);

    void resetAllResearch(Player player);

    default boolean meetsDependencies(Player player, @Nullable List<Identifier> dependencies) {
        if (dependencies == null) {
            return true;
        }
        return dependencies.stream().noneMatch(dependency -> getResearchState(player, dependency) != ResearchState.UNLOCKED);
    }
}
