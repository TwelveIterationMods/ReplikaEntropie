package net.blay09.mods.replikaentropie.recipe;

import net.blay09.mods.balm.world.item.crafting.BalmRecipeTypeRegistrar;
import net.blay09.mods.balm.world.item.crafting.DeferredRecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class ModRecipes {

    public static DeferredRecipeType<SingleRecipeInput, RecyclerRecipe> recycler;
    public static DeferredRecipeType<SingleRecipeInput, FragmentAcceleratorRecipe> fragmentAccelerator;
    public static DeferredRecipeType<SingleRecipeInput, FragmentalHeaterRecipe> fragmentalHeater;
    public static DeferredRecipeType<RecipeInput, FabricatorRecipe> fabricator;
    public static DeferredRecipeType<SingleRecipeInput, BiomassIncubatorRecipe> biomassIncubator;
    public static DeferredRecipeType<SingleRecipeInput, OreVacuumRecipe> oreVacuum;
    public static DeferredRecipeType<RecipeInput, MetalDetectorRecipe> metalDetector;
    public static DeferredRecipeType<RecipeInput, AssemblerRecipe> assembler;
    public static DeferredRecipeType<RecipeInput, ResearchRecipe> research;

    public static void initialize(BalmRecipeTypeRegistrar recipes) {
        recycler = recipes.register("recycler", RecyclerRecipe.class)
                .withSerializer(RecyclerRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        fragmentAccelerator = recipes.register("fragment_accelerator", FragmentAcceleratorRecipe.class)
                .withSerializer(FragmentAcceleratorRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        fragmentalHeater = recipes.register("fragmental_heater", FragmentalHeaterRecipe.class)
                .withSerializer(FragmentalHeaterRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        fabricator = recipes.register("fabricator", FabricatorRecipe.class)
                .withSerializer(FabricatorRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        biomassIncubator = recipes.register("biomass_incubator", BiomassIncubatorRecipe.class)
                .withSerializer(BiomassIncubatorRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        oreVacuum = recipes.register("ore_vacuum", OreVacuumRecipe.class)
                .withSerializer(OreVacuumRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        metalDetector = recipes.register("metal_detector", MetalDetectorRecipe.class)
                .withSerializer(MetalDetectorRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        assembler = recipes.register("assembler", AssemblerRecipe.class)
                .withSerializer(AssemblerRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        research = recipes.register("research", ResearchRecipe.class)
                .withSerializer(ResearchRecipe::serializer)
                .withRecipeBookCategory()
                .asDeferredRecipeType();
    }
}
