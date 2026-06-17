package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.FragmentalHeaterRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class ModFragmentalHeaterRecipeProvider extends FabricRecipeProvider {
    public ModFragmentalHeaterRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                final var fragmentTemperatureModifier = 1f / 10f;
                final var iceTemperatureModifier = -1f / 5f;
                fragmentalHeaterRecipe(ModItems.fragments.value(), 100, fragmentTemperatureModifier).save(output);
                fragmentalHeaterRecipe(Items.SNOWBALL, 0, iceTemperatureModifier).save(output);
                fragmentalHeaterRecipe(Items.ICE, 0, iceTemperatureModifier).save(output);
                fragmentalHeaterRecipe(Items.PACKED_ICE, 0, iceTemperatureModifier * 9).save(output);
                fragmentalHeaterRecipe(Items.BLUE_ICE, 0, iceTemperatureModifier * 27).save(output);
            }
        };
    }

    private FragmentalHeaterRecipeBuilder fragmentalHeaterRecipe(Item ingredient, int energy, float temperature) {
        return new FragmentalHeaterRecipeBuilder(Ingredient.of(ingredient), energy, temperature);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Fragmental Heater Recipes";
    }

    public record FragmentalHeaterRecipeBuilder(Ingredient ingredient, int energy, float temperature) {
        public void save(RecipeOutput output) {
            final var id = ReplikaEntropie.id("fragmental_heater/" + ingredientPath(ingredient));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new FragmentalHeaterRecipe(ingredient, energy, temperature), null);
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
