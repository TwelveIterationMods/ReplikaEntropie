package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PersistentResearchManager implements ResearchManager {

    private static final String TAG_RESEARCH = "ResearchStates";
    private static final String TAG_NONOGRAMS = "Nonograms";

    private static CompoundTag getPersistentData(Player player) {
        final var data = Balm.getHooks().getPersistentData(player);
        final var modData = data.getCompound(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        return modData;
    }

    @Override
    public ResearchState getResearchState(Player player, ResourceLocation id) {
        final var modData = getPersistentData(player);
        final var researchTag = modData.getCompound(TAG_RESEARCH);
        final var key = id.toString();
        final var ordinal = researchTag.contains(key) ? researchTag.getInt(key) : ResearchState.NONE.ordinal();
        return ResearchState.values()[ordinal];
    }

    @Override
    public Optional<NonogramState> getNonogramState(Player player, ResourceLocation id) {
        final var modData = getPersistentData(player);
        final var nonogramsTag = modData.getCompound(TAG_NONOGRAMS);
        final var key = id.toString();
        if (nonogramsTag.contains(key)) {
            final var stateTag = nonogramsTag.getCompound(key);
            return Optional.of(NonogramState.read(stateTag));
        }

        return Optional.empty();
    }

    @Override
    public void updateResearch(Player player, ResourceLocation id, ResearchState state) {
        final var modData = getPersistentData(player);
        final var researchTag = modData.getCompound(TAG_RESEARCH);
        researchTag.putInt(id.toString(), state.ordinal());
        modData.put(TAG_RESEARCH, researchTag);

        if (state == ResearchState.UNLOCKED) {
            player.level().getRecipeManager().byKey(id).ifPresent(recipe -> {
                if (recipe instanceof ResearchRecipe researchRecipe
                        && researchRecipe.type() == ResearchRecipe.Type.CRAFTING) {
                    player.awardRecipesByKey(researchRecipe.unlockedRecipes().toArray(ResourceLocation[]::new));
                }
            });
        }
    }

    @Override
    public void updateNonogram(Player player, ResourceLocation id, NonogramState state) {
        final var modData = getPersistentData(player);
        final var nonogramsTag = modData.getCompound(TAG_NONOGRAMS);
        nonogramsTag.put(id.toString(), state.write());
        modData.put(TAG_NONOGRAMS, nonogramsTag);
    }

    @Override
    public void resetAllResearch(Player player) {
        final var modData = getPersistentData(player);
        modData.remove(TAG_RESEARCH);
    }
}

