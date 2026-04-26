package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.AssemblerRecipe;
import net.blay09.mods.replikaentropie.recipe.CountedIngredient;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModAssemblerRecipeProvider extends FabricRecipeProvider {
    public ModAssemblerRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
        assemblerRecipe(ModBlocks.fabricator, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 1)
                .ingredient(Items.REDSTONE, 8)
                .ingredient(ModItems.scrap, 8)
                .ingredient(ModItems.fragments, 2)
                .save(output);

        assemblerRecipe(ModBlocks.replikaWorkbench, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.DIAMOND, 4)
                .ingredient(ModItems.chipset, 4)
                .ingredient(ModItems.scrap, 8)
                .ingredient(ModItems.fragments, 4)
                .save(output);

        assemblerRecipe(ModBlocks.entropicDataMiner, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(ModItems.chipset, 2)
                .ingredient(Items.COPPER_INGOT, 3)
                .ingredient(ModItems.fragments, 8)
                .ingredient(ModItems.biomass, 8)
                .save(output);

        assemblerRecipe(ModItems.nightVisionGoggles, 1)
                .ingredient(Items.IRON_INGOT, 2)
                .ingredient(Items.GLOWSTONE_DUST, 2)
                .ingredient(Items.GREEN_DYE, 2)
                .save(output);

        assemblerRecipe(ModItems.brightVisionGoggles, 1)
                .ingredient(Items.IRON_INGOT, 2)
                .ingredient(Items.GLOWSTONE_DUST, 2)
                .ingredient(Items.GLOW_INK_SAC, 2)
                .save(output);

        assemblerRecipe(ModItems.graviliftHarness, 1)
                .ingredient(Items.IRON_INGOT, 8)
                .ingredient(Items.FEATHER, 8)
                .ingredient(Items.GHAST_TEAR, 2)
                .ingredient(Items.NETHER_STAR, 1)
                .save(output);

        assemblerRecipe(ModItems.chipset, 1)
                .ingredient(Items.REDSTONE, 2)
                .ingredient(Items.IRON_INGOT, 1)
                .ingredient(Items.GREEN_DYE, 1)
                .save(output);

        assemblerRecipe(ModBlocks.biomassIncubator, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(ModItems.biomass, 4)
                .ingredient(Items.GLASS, 4)
                .ingredient(Items.DIRT, 3)
                .ingredient(Items.WATER_BUCKET, 1)
                .save(output);

        assemblerRecipe(ModBlocks.biomassHarvester, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.RAIL, 8)
                .ingredient(Items.STICK, 4)
                .ingredient(Items.HOPPER, 1)
                .ingredient(Items.LEAD, 1)
                .save(output);

        assemblerRecipe(ModBlocks.cobblescrap, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 6)
                .ingredient(Items.FLINT, 3)
                .ingredient(Items.WATER_BUCKET, 1)
                .ingredient(Items.LAVA_BUCKET, 1)
                .save(output);

        assemblerRecipe(ModBlocks.lavascrap, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 6)
                .ingredient(Items.BRICK, 4)
                .ingredient(Items.DIAMOND, 3)
                .save(output);

        assemblerRecipe(ModBlocks.worldEater, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.DIAMOND, 3)
                .ingredient(Items.GLASS, 2)
                .ingredient(Items.SPYGLASS, 1)
                .ingredient(Items.FISHING_ROD, 1)
                .save(output);

        assemblerRecipe(ModBlocks.fragmentalGenerator, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 8)
                .ingredient(Items.GUNPOWDER, 4)
                .ingredient(Items.BLAZE_POWDER, 2)
                .save(output);

        assemblerRecipe(ModBlocks.fragmentAccelerator, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.POWERED_RAIL, 8)
                .ingredient(Items.COPPER_INGOT, 3)
                .ingredient(Items.REDSTONE_TORCH, 1)
                .ingredient(Items.CLOCK, 1)
                .save(output);

        assemblerRecipe(ModBlocks.chaosEngine, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.NETHER_STAR, 1)
                .ingredient(Items.RECOVERY_COMPASS, 1)
                .ingredient(Items.CLOCK, 1)
                .ingredient(Items.COMPASS, 1)
                .save(output);

        assemblerRecipe(ModBlocks.waterSink, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 3)
                .ingredient(Items.WATER_BUCKET, 2)
                .save(output);

        assemblerRecipe(ModBlocks.lavaSink, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 3)
                .ingredient(Items.LAVA_BUCKET, 2)
                .save(output);

        assemblerRecipe(ModBlocks.solarSink, 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 3)
                .ingredient(Items.REDSTONE, 2)
                .ingredient(Items.DAYLIGHT_DETECTOR, 1)
                .save(output);

        assemblerRecipe(ModItems.nullphaser, 1)
                .ingredient(Items.IRON_INGOT, 2)
                .ingredient(Items.ENDER_PEARL, 1)
                .ingredient(Items.GLASS, 1)
                .save(output);

        assemblerRecipe(ModItems.oreVacuum, 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.HOPPER, 1)
                .ingredient(Items.STICK, 1)
                .save(output);

        assemblerRecipe(ModItems.slowphasers, 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.ICE, 2)
                .save(output);

        assemblerRecipe(ModItems.magphasers, 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.ECHO_SHARD, 2)
                .save(output);

        assemblerRecipe(ModItems.stompers, 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.OBSIDIAN, 2)
                .save(output);

        assemblerRecipe(ModItems.springBoots, 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.IRON_CHAIN, 2)
                .save(output);

        assemblerRecipe(ModItems.semisonicSpeeders, 1)
                .ingredient(Items.IRON_INGOT, 7)
                .ingredient(Items.BLAZE_ROD, 2)
                .save(output);
            }
        };
    }

    public static AssemblerRecipeBuilder assemblerRecipe(ItemLike result, int count) {
        return new AssemblerRecipeBuilder(new ItemStackTemplate(result.asItem()), count);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Assembler Recipes";
    }

    public static class AssemblerRecipeBuilder {
        private final ItemStackTemplate result;
        private final int count;
        private final List<CountedIngredient> ingredients = new java.util.ArrayList<>();

        public AssemblerRecipeBuilder(ItemStackTemplate result, int count) {
            this.result = result;
            this.count = count;
        }

        public AssemblerRecipeBuilder ingredient(ItemLike item, int count) {
            return ingredient(Ingredient.of(item), count);
        }

        public AssemblerRecipeBuilder ingredient(Ingredient ingredient, int count) {
            this.ingredients.add(new CountedIngredient(ingredient, count));
            return this;
        }

        public void save(RecipeOutput output) {
            final var item = result.item().value();
            final var pathName = BuiltInRegistries.ITEM.getKey(item).getPath();
            final var id = ReplikaEntropie.id("assembler/" + pathName + (count > 1 ? "_" + count : ""));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new AssemblerRecipe(ingredients, result.withCount(count)), null);
        }
    }
}
