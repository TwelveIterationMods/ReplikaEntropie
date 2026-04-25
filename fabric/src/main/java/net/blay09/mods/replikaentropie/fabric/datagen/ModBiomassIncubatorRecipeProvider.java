package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.recipe.BiomassIncubatorRecipe;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

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
                incubatorRecipe(Ingredient.of(Items.WHEAT_SEEDS), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.WHEAT)).save(output);
                incubatorRecipe(Ingredient.of(Items.BEETROOT_SEEDS), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.BEETROOT)).save(output);
                incubatorRecipe(Ingredient.of(Items.PUMPKIN_SEEDS), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.PUMPKIN)).save(output);
                incubatorRecipe(Ingredient.of(Items.MELON_SEEDS), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.MELON)).save(output);
                incubatorRecipe(Ingredient.of(Items.TORCHFLOWER_SEEDS), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.TORCHFLOWER)).save(output);
                incubatorRecipe(Ingredient.of(Items.CARROT), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.CARROT)).save(output);
                incubatorRecipe(Ingredient.of(Items.POTATO), tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE), new ItemStackTemplate(Items.POTATO)).save(output);
                incubatorRecipe(Ingredient.of(Items.NETHER_WART), Ingredient.of(Items.SOUL_SAND), new ItemStackTemplate(Items.NETHER_WART)).save(output);
                incubatorRecipe(Ingredient.of(Items.CHORUS_FRUIT), Ingredient.of(Items.END_STONE), new ItemStackTemplate(Items.CHORUS_PLANT)).save(output);
            }
        };
    }

    private BiomassIncubatorRecipeBuilder incubatorRecipe(Item seed, Item soil, Item output) {
        return incubatorRecipe(Ingredient.of(seed), Ingredient.of(soil), new ItemStackTemplate(output));
    }

    private BiomassIncubatorRecipeBuilder incubatorRecipe(Ingredient seed, Ingredient soil, ItemStackTemplate output) {
        return new BiomassIncubatorRecipeBuilder(seed, soil, output);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Biomass Incubator Recipes";
    }

    public record BiomassIncubatorRecipeBuilder(Ingredient seed, Ingredient soil, ItemStackTemplate result) {
        public void save(RecipeOutput output) {
            final var id = ReplikaEntropie.id("biomass_incubator/" + ingredientPath(seed));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new BiomassIncubatorRecipe(seed, soil, result), null);
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
