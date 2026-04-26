package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.FragmentalGeneratorRecipe;
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

public class ModFragmentalGeneratorRecipeProvider extends FabricRecipeProvider {
    public ModFragmentalGeneratorRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                fragmentalGeneratorRecipe(ModItems.fragments.value(), 100, 0.1f).save(output);
                final var iceTemperatureModifier = -0.1f;
                fragmentalGeneratorRecipe(Items.SNOWBALL, 0, iceTemperatureModifier).save(output);
                fragmentalGeneratorRecipe(Items.ICE, 0, iceTemperatureModifier).save(output);
                fragmentalGeneratorRecipe(Items.PACKED_ICE, 0, iceTemperatureModifier * 9).save(output);
                fragmentalGeneratorRecipe(Items.BLUE_ICE, 0, iceTemperatureModifier * 9 * 9).save(output);
                fragmentalGeneratorRecipe(Items.GUNPOWDER, 0, 0.1f).save(output);
                fragmentalGeneratorRecipe(Items.BLAZE_POWDER, 0, 0.2f).save(output);
                fragmentalGeneratorRecipe(Items.FIRE_CHARGE, 0, 0.4f).save(output);
            }
        };
    }

    private FragmentalGeneratorRecipeBuilder fragmentalGeneratorRecipe(Item ingredient, int energy, float temperature) {
        return new FragmentalGeneratorRecipeBuilder(Ingredient.of(ingredient), energy, temperature);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Fragmental Generator Recipes";
    }

    public record FragmentalGeneratorRecipeBuilder(Ingredient ingredient, int energy, float temperature) {
        public void save(RecipeOutput output) {
            final var id = ReplikaEntropie.id("fragmental_generator/" + ingredientPath(ingredient));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new FragmentalGeneratorRecipe(ingredient, energy, temperature), null);
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
