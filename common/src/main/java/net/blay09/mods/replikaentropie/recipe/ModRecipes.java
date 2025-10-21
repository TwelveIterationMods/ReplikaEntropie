package net.blay09.mods.replikaentropie.recipe;

import net.blay09.mods.balm.api.recipe.BalmRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModRecipes {

    public static RecipeType<RecyclerRecipe> recyclerType;
    public static RecipeSerializer<RecyclerRecipe> recyclerSerializer;
    public static RecipeType<FabricatorRecipe> fabricatorType;
    public static RecipeSerializer<FabricatorRecipe> fabricatorSerializer;
    public static RecipeType<BiomassIncubatorRecipe> biomassIncubatorType;
    public static RecipeSerializer<BiomassIncubatorRecipe> biomassIncubatorSerializer;
    public static RecipeType<VacuumableOreRecipe> vacuumableOreType;
    public static RecipeSerializer<VacuumableOreRecipe> vacuumableOreSerializer;
    public static RecipeType<AssemblerRecipe> assemblerType;
    public static RecipeSerializer<AssemblerRecipe> assemblerSerializer;
    public static RecipeType<ResearchRecipe> researchType;
    public static RecipeSerializer<ResearchRecipe> researchSerializer;

    public static void initialize(BalmRecipes recipes) {
        recipes.registerRecipeType((identifier) -> recyclerType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier.getPath();
            }
        }, id("recycler"));
        recipes.registerRecipeSerializer(() -> recyclerSerializer = new RecyclerRecipe.Serializer(), id("recycler"));

        recipes.registerRecipeType((identifier) -> fabricatorType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier.getPath();
            }
        }, id("fabricator"));
        recipes.registerRecipeSerializer(() -> fabricatorSerializer = new FabricatorRecipe.Serializer(), id("fabricator"));

        recipes.registerRecipeType((identifier) -> biomassIncubatorType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier.getPath();
            }
        }, id("biomass_incubator"));
        recipes.registerRecipeSerializer(() -> biomassIncubatorSerializer = new BiomassIncubatorRecipe.Serializer(), id("biomass_incubator"));

        recipes.registerRecipeType((identifier) -> vacuumableOreType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier.getPath();
            }
        }, id("vacuumable_ore"));
        recipes.registerRecipeSerializer(() -> vacuumableOreSerializer = new VacuumableOreRecipe.Serializer(), id("vacuumable_ore"));

        recipes.registerRecipeType((identifier) -> assemblerType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier.getPath();
            }
        }, id("assembler"));
        recipes.registerRecipeSerializer(() -> assemblerSerializer = new AssemblerRecipe.Serializer(), id("assembler"));

        recipes.registerRecipeType((identifier) -> researchType = new RecipeType<>() {
            @Override
            public String toString() {
                return identifier.getPath();
            }
        }, id("research"));
        recipes.registerRecipeSerializer(() -> researchSerializer = new ResearchRecipe.Serializer(), id("research"));
    }
}
