package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.concurrent.CompletableFuture;

public class ModFabricatorRecipeProvider extends FabricRecipeProvider {
    public ModFabricatorRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
        int sortOrder = 0;
        fabricatorRecipe(Items.IRON_CHAIN, 2, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.COPPER_CHAIN.unaffected(), 2, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.STICK, 0, 1, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.LEVER, 1, 1, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.TRIPWIRE_HOOK, 1, 1, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.REDSTONE_TORCH, 0, 1, 1, sortOrder += 100).save(output);
        fabricatorRecipe(Items.REPEATER, 1, 1, 1, sortOrder += 100).save(output);
        fabricatorRecipe(Items.COMPARATOR, 2, 1, 1, sortOrder += 100).save(output);
        fabricatorRecipe(Items.SCAFFOLDING, 1, 1, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.BONE_MEAL, 0, 2, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.FLINT, 1, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.SLIME_BALL, 0, 8, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.GUNPOWDER, 2, 0, 2, sortOrder += 100).save(output);
        fabricatorRecipe(Items.SNOWBALL, 0, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.ICE, 0, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.RAIL, 1, 1, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.ACTIVATOR_RAIL, 1, 1, 1, sortOrder += 100).save(output);
        fabricatorRecipe(Items.DETECTOR_RAIL, 1, 1, 1, sortOrder += 100).save(output);
        fabricatorRecipe(Items.POWERED_RAIL, 2, 1, 1, sortOrder += 100).save(output);
        fabricatorRecipe(Items.BOWL, 1, 1, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.BRICK, 2, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.FLOWER_POT, 4, 0, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.PAPER, 0, 2, 0, sortOrder += 100).save(output);
        fabricatorRecipe(Items.GLASS, 1, 0, 0, sortOrder += 100).save(output);
            }
        };
    }

    private FabricatorRecipeBuilder fabricatorRecipe(ItemLike result, int scrap, int biomass, int fragments, int sortOrder) {
        return new FabricatorRecipeBuilder(new ItemStackTemplate(result.asItem()), scrap, biomass, fragments, sortOrder);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Fabricator Recipes";
    }

    public record FabricatorRecipeBuilder(ItemStackTemplate result, int scrap, int biomass, int fragments, int sortOrder) {
        public void save(RecipeOutput output) {
            final var item = result.item().value();
            final var pathName = BuiltInRegistries.ITEM.getKey(item).getPath();
            final var id = ReplikaEntropie.id("fabricator/" + pathName);
            output.accept(ResourceKey.create(Registries.RECIPE, id), new FabricatorRecipe(scrap, biomass, fragments, result, sortOrder), null);
        }
    }
}
