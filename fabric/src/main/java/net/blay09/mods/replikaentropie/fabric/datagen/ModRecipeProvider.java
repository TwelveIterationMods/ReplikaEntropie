package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                shapeless(RecipeCategory.REDSTONE, ModItems.chipset)
                        .requires(ModItems.damagedChipset)
                        .requires(Items.REDSTONE)
                        .requires(Items.STRING)
                        .unlockedBy("has_damaged_chipset", has(ModItems.damagedChipset))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.handheldAnalyzer)
                        .pattern("IIG")
                        .pattern("ICI")
                        .pattern("II ")
                        .define('I', Items.IRON_INGOT)
                        .define('C', ModItems.chipset)
                        .define('G', Items.GLASS)
                        .unlockedBy("has_redstone", has(Items.REDSTONE))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.skyScraper)
                        .pattern("III")
                        .pattern("GCG")
                        .pattern("III")
                        .define('I', Items.IRON_INGOT)
                        .define('C', ModItems.chipset)
                        .define('G', Items.GLASS)
                        .unlockedBy("has_chipset", has(ModItems.chipset))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.automaticHackTool)
                        .pattern("IC")
                        .pattern(" R")
                        .define('I', Items.IRON_INGOT)
                        .define('C', Items.COPPER_INGOT)
                        .define('R', Items.REDSTONE_TORCH)
                        .unlockedBy("has_redstone", has(Items.REDSTONE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModBlocks.recycler)
                        .pattern("IBI")
                        .pattern("CSC")
                        .pattern("III")
                        .define('I', Items.IRON_INGOT)
                        .define('B', ModItems.makeshiftPSU)
                        .define('S', Items.SHEARS)
                        .define('C', Items.IRON_CHAIN)
                        .unlockedBy("has_quartz", has(Items.QUARTZ))
                        .save(output);

                shaped(RecipeCategory.MISC, ModBlocks.assembler)
                        .pattern("IBI")
                        .pattern("CAC")
                        .pattern("III")
                        .define('I', Items.IRON_INGOT)
                        .define('B', ModItems.makeshiftPSU)
                        .define('A', Items.ANVIL)
                        .define('C', Items.IRON_CHAIN)
                        .unlockedBy("has_quartz", has(Items.QUARTZ))
                        .save(output);

                shaped(RecipeCategory.MISC, ModBlocks.wasteBarrel)
                        .pattern("ISI")
                        .pattern("DOD")
                        .pattern("ISI")
                        .define('I', Items.IRON_INGOT)
                        .define('D', Items.LIME_DYE)
                        .define('O', Items.OBSIDIAN)
                        .define('S', ModItems.scrap)
                        .unlockedBy("has_obsidian", has(Items.OBSIDIAN))
                        .save(output);

                shapeless(RecipeCategory.COMBAT, ModItems.biosteel)
                        .requires(Items.IRON_INGOT)
                        .requires(ModItems.scrap)
                        .requires(ModItems.scrap)
                        .requires(ModItems.biomass)
                        .requires(ModItems.biomass)
                        .requires(ModItems.biomass)
                        .unlockedBy("has_biomass", has(ModItems.biomass))
                        .save(output);

                shapeless(RecipeCategory.COMBAT, ModItems.hazmatLining, 4)
                        .requires(Items.LEATHER)
                        .requires(Items.ARMADILLO_SCUTE)
                        .requires(Items.YELLOW_DYE)
                        .unlockedBy("has_armadillo_scute", has(Items.ARMADILLO_SCUTE))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.metalDetector)
                        .pattern("  R")
                        .pattern(" I ")
                        .pattern("C  ")
                        .define('R', Items.COMPARATOR)
                        .define('I', Items.IRON_INGOT)
                        .define('C', Items.COPPER_INGOT)
                        .unlockedBy("has_quartz", has(Items.QUARTZ))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.makeshiftPSU)
                        .pattern(" PC")
                        .pattern("TRT")
                        .pattern("CP ")
                        .define('P', Items.PISTON)
                        .define('C', Items.COPPER_INGOT)
                        .define('R', Items.REDSTONE)
                        .define('T', Items.REPEATER)
                        .unlockedBy("has_redstone", has(Items.REDSTONE))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.biosteelHelmet)
                        .pattern("BBB")
                        .pattern("B B")
                        .define('B', ModItems.biosteel)
                        .unlockedBy("has_biosteel", has(ModItems.biosteel))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.biosteelChestplate)
                        .pattern("B B")
                        .pattern("BBB")
                        .pattern("BBB")
                        .define('B', ModItems.biosteel)
                        .unlockedBy("has_biosteel", has(ModItems.biosteel))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.biosteelLeggings)
                        .pattern("BBB")
                        .pattern("B B")
                        .pattern("B B")
                        .define('B', ModItems.biosteel)
                        .unlockedBy("has_biosteel", has(ModItems.biosteel))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.biosteelBoots)
                        .pattern("B B")
                        .pattern("B B")
                        .define('B', ModItems.biosteel)
                        .unlockedBy("has_biosteel", has(ModItems.biosteel))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.hazmatHelmet)
                        .pattern("BBB")
                        .pattern("B B")
                        .define('B', ModItems.hazmatLining)
                        .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.hazmatChestplate)
                        .pattern("B B")
                        .pattern("BBB")
                        .pattern("BBB")
                        .define('B', ModItems.hazmatLining)
                        .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.hazmatLeggings)
                        .pattern("BBB")
                        .pattern("B B")
                        .pattern("B B")
                        .define('B', ModItems.hazmatLining)
                        .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.hazmatBoots)
                        .pattern("B B")
                        .pattern("B B")
                        .define('B', ModItems.hazmatLining)
                        .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                        .save(output);

                shapeless(RecipeCategory.FOOD, ModItems.biomash)
                        .requires(ModItems.biomass)
                        .requires(Items.BOWL)
                        .unlockedBy("has_biomass", has(ModItems.biomass))
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID;
    }
}
