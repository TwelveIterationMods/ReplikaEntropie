package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.recipe.OreVacuumRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModVacuumableOreRecipeProvider extends FabricRecipeProvider {
    public ModVacuumableOreRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                oreVacuum(Ingredient.of(Items.COAL_ORE), Blocks.STONE).save(output, "coal_ore");
                oreVacuum(Ingredient.of(Items.IRON_ORE), Blocks.STONE).save(output, "iron_ore");
                oreVacuum(Ingredient.of(Items.COPPER_ORE), Blocks.STONE).save(output, "copper_ore");
                oreVacuum(Ingredient.of(Items.GOLD_ORE), Blocks.STONE).save(output, "gold_ore");
                oreVacuum(Ingredient.of(Items.REDSTONE_ORE), Blocks.STONE).save(output, "redstone_ore");
                oreVacuum(Ingredient.of(Items.LAPIS_ORE), Blocks.STONE).save(output, "lapis_ore");
                oreVacuum(Ingredient.of(Items.DIAMOND_ORE), Blocks.STONE).save(output, "diamond_ore");
                oreVacuum(Ingredient.of(Items.EMERALD_ORE), Blocks.STONE).save(output, "emerald_ore");

                oreVacuum(Ingredient.of(Items.DEEPSLATE_COAL_ORE), Blocks.DEEPSLATE).save(output, "deepslate_coal_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_IRON_ORE), Blocks.DEEPSLATE).save(output, "deepslate_iron_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_COPPER_ORE), Blocks.DEEPSLATE).save(output, "deepslate_copper_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_GOLD_ORE), Blocks.DEEPSLATE).save(output, "deepslate_gold_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_REDSTONE_ORE), Blocks.DEEPSLATE).save(output, "deepslate_redstone_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_LAPIS_ORE), Blocks.DEEPSLATE).save(output, "deepslate_lapis_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_DIAMOND_ORE), Blocks.DEEPSLATE).save(output, "deepslate_diamond_ore");
                oreVacuum(Ingredient.of(Items.DEEPSLATE_EMERALD_ORE), Blocks.DEEPSLATE).save(output, "deepslate_emerald_ore");

                oreVacuum(Ingredient.of(Items.NETHER_QUARTZ_ORE), Blocks.NETHERRACK).save(output, "nether_quartz_ore");
                oreVacuum(Ingredient.of(Items.NETHER_GOLD_ORE), Blocks.NETHERRACK).save(output, "nether_gold_ore");

                oreVacuum(Ingredient.of(Items.ANCIENT_DEBRIS), Blocks.NETHERRACK).save(output, "ancient_debris");
            }
        };
    }

    private OreVacuumRecipeBuilder oreVacuum(Ingredient ingredient, Block emptyBlock) {
        return new OreVacuumRecipeBuilder(ingredient, emptyBlock);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Vacuumable Ore Recipes";
    }

    public record OreVacuumRecipeBuilder(Ingredient ingredient, Block emptyBlock) {
        public void save(RecipeOutput output, String name) {
            final var id = ReplikaEntropie.id("ore_vacuum/" + name);
            output.accept(ResourceKey.create(Registries.RECIPE, id), new OreVacuumRecipe(ingredient, emptyBlock), null);
        }
    }
}
