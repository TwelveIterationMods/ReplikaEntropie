package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PersistentResearchManager implements ResearchManager {

    private static final String TAG_RESEARCH = "ResearchStates";
    private static final String TAG_NONOGRAMS = "Nonograms";

    private static CompoundTag getPersistentData(Player player) {
        final var data = Balm.hooks().getPersistentData(player);
        final var modData = data.getCompoundOrEmpty(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        return modData;
    }

    @Override
    public ResearchState getResearchState(Player player, Identifier id) {
        final var modData = getPersistentData(player);
        final var researchTag = modData.getCompoundOrEmpty(TAG_RESEARCH);
        final var key = id.toString();
        final var ordinal = researchTag.getIntOr(key, ResearchState.NONE.ordinal());
        return ResearchState.values()[ordinal];
    }

    @Override
    public Optional<NonogramState> getNonogramState(Player player, Identifier id) {
        final var modData = getPersistentData(player);
        final var nonogramsTag = modData.getCompoundOrEmpty(TAG_NONOGRAMS);
        final var key = id.toString();
        if (nonogramsTag.contains(key)) {
            final var stateTag = nonogramsTag.getCompoundOrEmpty(key);
            return Optional.of(NonogramState.read(stateTag));
        }

        return Optional.empty();
    }

    @Override
    public void updateResearch(Player player, Identifier id, ResearchState state) {
        final var modData = getPersistentData(player);
        final var researchTag = modData.getCompoundOrEmpty(TAG_RESEARCH);
        researchTag.putInt(id.toString(), state.ordinal());
        modData.put(TAG_RESEARCH, researchTag);

        if (state == ResearchState.UNLOCKED && player instanceof ServerPlayer serverPlayer
                && player.level() instanceof ServerLevel serverLevel) {
            serverLevel.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id)).ifPresent(recipe -> {
                if (recipe.value() instanceof ResearchRecipe researchRecipe
                        && researchRecipe.type() == ResearchRecipe.Type.CRAFTING) {
                    serverPlayer.awardRecipesByKey(researchRecipe.unlockedRecipes().stream().map(it -> ResourceKey.create(Registries.RECIPE, it)).toList());
                }
            });
        }
    }

    @Override
    public void updateNonogram(Player player, Identifier id, NonogramState state) {
        final var modData = getPersistentData(player);
        final var nonogramsTag = modData.getCompoundOrEmpty(TAG_NONOGRAMS);
        nonogramsTag.put(id.toString(), state.write());
        modData.put(TAG_NONOGRAMS, nonogramsTag);
    }

    @Override
    public void resetAllResearch(Player player) {
        final var modData = getPersistentData(player);
        modData.remove(TAG_RESEARCH);
    }
}
