package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.recipe.BiomassIncubatorRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class ModBiomassIncubatorRecipeProvider extends FabricRecipeProvider {
    public ModBiomassIncubatorRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                incubatorRecipe(Items.WHEAT_SEEDS, Items.WHEAT, 0.2f).save(output);
                incubatorRecipe(Items.BEETROOT_SEEDS, Items.BEETROOT, 0.2f).save(output);
                incubatorRecipe(Items.PUMPKIN_SEEDS, Items.PUMPKIN, 0.2f).save(output);
                incubatorRecipe(Items.MELON_SEEDS, Items.MELON, 0.2f).save(output);
                incubatorRecipe(Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, 0.2f).save(output);
                incubatorRecipe(Items.CARROT, Items.CARROT, 0.2f).save(output);
                incubatorRecipe(Items.POTATO, Items.POTATO, 0.2f).save(output);
            }
        };
    }

    private BiomassIncubatorRecipeBuilder incubatorRecipe(Item input, Item output, float biomass) {
        return new BiomassIncubatorRecipeBuilder(Ingredient.of(input), new ItemStackTemplate(output), biomass);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Biomass Incubator Recipes";
    }

    public record BiomassIncubatorRecipeBuilder(Ingredient ingredient, ItemStackTemplate result, float biomass) {
        public void save(RecipeOutput output) {
            final var id = ReplikaEntropie.id("biomass_incubator/" + ingredientPath(ingredient));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new BiomassIncubatorRecipe(ingredient, result, biomass), null);
        }

        private static String ingredientPath(Ingredient ingredient) {
            return ingredient.items()
                    .findFirst()
                    .map(Holder::value)
                    .map(BuiltInRegistries.ITEM::getKey)
                    .map(Identifier::getPath)
                    .orElseThrow();
        }
    }
}
