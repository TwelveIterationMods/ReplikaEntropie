package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        shapeless(RecipeCategory.REDSTONE, ModItems.chipset)
                .requires(ModItems.damagedChipset)
                .requires(Items.REDSTONE)
                .requires(Items.STRING)
                .unlockedBy("has_damaged_chipset", has(ModItems.damagedChipset))
                .save(exporter);

        shaped(RecipeCategory.TOOLS, ModItems.handheldAnalyzer)
                .pattern("IIG")
                .pattern("IRI")
                .pattern("II ")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('G', Items.GLASS)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(exporter);

        shaped(RecipeCategory.TOOLS, ModItems.skyScraper)
                .pattern("III")
                .pattern("GCG")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('C', ModItems.chipset)
                .define('G', Items.GLASS)
                .unlockedBy("has_chipset", has(ModItems.chipset))
                .save(exporter);

        shaped(RecipeCategory.MISC, ModBlocks.recycler)
                .pattern("IDI")
                .pattern("CSC")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('D', Items.DAYLIGHT_DETECTOR)
                .define('S', Items.SHEARS)
                .define('C', Items.CHAIN)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(exporter);

        shaped(RecipeCategory.MISC, ModBlocks.assembler)
                .pattern("IDI")
                .pattern("CAC")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('D', Items.DAYLIGHT_DETECTOR)
                .define('A', Items.ANVIL)
                .define('C', Items.CHAIN)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(exporter);

        shapeless(RecipeCategory.COMBAT, ModItems.biosteel)
                .requires(Items.IRON_INGOT)
                .requires(ModItems.scrap)
                .requires(ModItems.scrap)
                .requires(ModItems.biomass)
                .requires(ModItems.biomass)
                .requires(ModItems.biomass)
                .unlockedBy("has_biomass", has(ModItems.biomass))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.biosteelHelmet)
                .pattern("BBB")
                .pattern("B B")
                .define('B', ModItems.biosteel)
                .unlockedBy("has_biosteel", has(ModItems.biosteel))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.biosteelChestplate)
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.biosteel)
                .unlockedBy("has_biosteel", has(ModItems.biosteel))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.biosteelLeggings)
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.biosteel)
                .unlockedBy("has_biosteel", has(ModItems.biosteel))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.biosteelBoots)
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.biosteel)
                .unlockedBy("has_biosteel", has(ModItems.biosteel))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.hazmatHelmet)
                .pattern("BBB")
                .pattern("B B")
                .define('B', ModItems.hazmatLining)
                .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.hazmatChestplate)
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.hazmatLining)
                .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.hazmatLeggings)
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.hazmatLining)
                .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.hazmatBoots)
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.hazmatLining)
                .unlockedBy("has_hazmat_lining", has(ModItems.hazmatLining))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.replikaHelmetFrame)
                .pattern("BDB")
                .pattern("B B")
                .define('B', ModItems.replikaSkin)
                .define('D', Items.DIAMOND)
                .unlockedBy("has_replika_framing", has(ModItems.replikaSkin))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.replikaChestplateFrame)
                .pattern("B B")
                .pattern("BDB")
                .pattern("BBB")
                .define('B', ModItems.replikaSkin)
                .define('D', Items.DIAMOND)
                .unlockedBy("has_replika_framing", has(ModItems.replikaSkin))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.replikaLeggingsFrame)
                .pattern("BDB")
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.replikaSkin)
                .define('D', Items.DIAMOND)
                .unlockedBy("has_replika_framing", has(ModItems.replikaSkin))
                .save(exporter);

        shaped(RecipeCategory.COMBAT, ModItems.replikaBootsFrame)
                .pattern("D D")
                .pattern("B B")
                .define('B', ModItems.replikaSkin)
                .define('D', Items.DIAMOND)
                .unlockedBy("has_replika_framing", has(ModItems.replikaSkin))
                .save(exporter);

        shapeless(RecipeCategory.FOOD, ModItems.biomash)
                .requires(ModItems.biomass)
                .requires(Items.BOWL)
                .unlockedBy("has_biomass", has(ModItems.biomass))
                .save(exporter);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID;
    }
}
