package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModResearchRecipeProvider extends FabricRecipeProvider {
    public ModResearchRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                int sortOrder = 0;
                research(id("sky_scraper"))
                        .icon(ModItems.skyScraper)
                        .nonogram(id("sky_scraper"))
                        .unlocksRecipe(id("handheld_analyzer"))
                        .type(ResearchRecipe.Type.CRAFTING)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("handheld_analyzer"))
                        .icon(ModItems.handheldAnalyzer)
                        .dependsOn(id("research/sky_scraper"))
                        .nonogram(id("handheld_analyzer"))
                        .costs(0, 0, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("automatic_hack_tool"))
                        .icon(ModItems.automaticHackTool)
                        .dependsOn(id("research/handheld_analyzer"))
                        .nonogram(id("automatic_hack_tool"))
                        .costs(1, 0, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("recycler"))
                        .icon(ModBlocks.recycler)
                        .dependsOn(id("research/handheld_analyzer"))
                        .unlocksRecipe(id("recycler"))
                        .type(ResearchRecipe.Type.CRAFTING)
                        .nonogram(id("recycler"))
                        .costs(5, 0, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("entropic_energy"))
                        .icon(Items.DAYLIGHT_DETECTOR)
                        .dependsOn(id("research/recycler"))
                        .nonogram(id("entropic_energy"))
                        .costs(1, 0, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("water_sink"))
                        .icon(ModBlocks.waterSink)
                        .dependsOn(id("research/entropic_energy"))
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/water_sink"))
                        .nonogram(id("water_sink"))
                        .costs(4, 2, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("solar_sink"))
                        .icon(ModBlocks.solarSink)
                        .dependsOn(id("research/entropic_energy"))
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/solar_sink"))
                        .nonogram(id("solar_sink"))
                        .costs(4, 2, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("lava_sink"))
                        .icon(ModBlocks.lavaSink)
                        .dependsOn(id("research/entropic_energy"))
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/lava_sink"))
                        .nonogram(id("lava_sink"))
                        .costs(4, 2, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("scrap"))
                        .icon(ModItems.scrap)
                        .dependsOn(id("research/recycler"))
                        .nonogram(id("scrap"))
                        .costs(1, 1, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("biomass"))
                        .icon(ModItems.biomass)
                        .dependsOn(id("research/recycler"))
                        .nonogram(id("biomass"))
                        .costs(1, 0, 1, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("fragments"))
                        .icon(ModItems.fragments)
                        .dependsOn(id("research/recycler"))
                        .nonogram(id("fragments"))
                        .costs(1, 0, 0, 1)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("biosteel"))
                        .icon(ModItems.biosteel)
                        .dependsOn(id("research/scrap"))
                        .dependsOn(id("research/biomass"))
                        .nonogram(id("biosteel"))
                        .costs(1, 1, 1, 0)
                        .type(ResearchRecipe.Type.CRAFTING)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("assembler"))
                        .icon(ModBlocks.assembler)
                        .dependsOn(id("research/scrap"))
                        .dependsOn(id("research/biomass"))
                        .dependsOn(id("research/fragments"))
                        .nonogram(id("assembler"))
                        .unlocksRecipe(id("assembler"))
                        .costs(5, 2, 2, 2)
                        .type(ResearchRecipe.Type.CRAFTING)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("assembly_ticket"))
                        .icon(ModItems.assemblyTicket)
                        .dependsOn(id("research/assembler"))
                        .nonogram(id("assembly_ticket"))
                        .costs(1, 0, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("fabricator"))
                        .icon(ModBlocks.assembler)
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/fabricator"))
                        .nonogram(id("fabricator"))
                        .costs(5, 2, 2, 2)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("burst_energy"))
                        .icon(Items.LIGHTNING_ROD)
                        .dependsOn(id("research/assembler"))
                        .nonogram(id("burst_energy"))
                        .costs(5, 12, 12, 6)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("chipset"))
                        .icon(ModItems.chipset)
                        .dependsOn(id("research/assembler"))
                        .nonogram(id("chipset"))
                        .costs(4, 1, 0, 1)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("ore_vacuum"))
                        .icon(ModItems.oreVacuum)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/ore_vacuum"))
                        .nonogram(id("ore_vacuum"))
                        .costs(10, 8, 0, 2)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("slowphasers"))
                        .icon(ModItems.slowphasers)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/slowphasers"))
                        .nonogram(id("slowphasers"))
                        .costs(10, 4, 0, 4)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("magphasers"))
                        .icon(ModItems.magphasers)
                        .dependsOn(id("research/slowphasers"))
                        .unlocksRecipe(id("assembler/magphasers"))
                        .nonogram(id("magphasers"))
                        .costs(20, 4, 0, 8)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("nullphaser"))
                        .icon(ModItems.nullphaser)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/nullphaser"))
                        .nonogram(id("nullphaser"))
                        .costs(8, 4, 0, 8)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("semisonic_speeders"))
                        .icon(ModItems.semisonicSpeeders)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/semisonic_speeders"))
                        .nonogram(id("semisonic_speeders"))
                        .costs(12, 8, 4, 2)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("spring_boots"))
                        .icon(ModItems.springs)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/spring_boots"))
                        .nonogram(id("spring_boots"))
                        .costs(12, 8, 2, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("stompers"))
                        .icon(ModItems.stompers)
                        .dependsOn(id("research/spring_boots"))
                        .unlocksRecipe(id("assembler/stompers"))
                        .nonogram(id("stompers"))
                        .costs(24, 8, 0, 2)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("nightvision_goggles"))
                        .icon(ModItems.nightVisionGoggles)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/nightvision_goggles"))
                        .nonogram(id("nightvision_goggles"))
                        .costs(12, 4, 0, 4)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("brightvision_goggles"))
                        .icon(ModItems.brightVisionGoggles)
                        .dependsOn(id("research/nightvision_goggles"))
                        .unlocksRecipe(id("assembler/brightvision_goggles"))
                        .nonogram(id("brightvision_goggles"))
                        .costs(24, 8, 8, 16)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("gravilift_harness"))
                        .icon(ModItems.graviliftHarness)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/gravilift_harness"))
                        .nonogram(id("gravilift_harness"))
                        .costs(32, 16, 4, 32)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("cobblescrap"))
                        .icon(ModBlocks.cobblescrap)
                        .dependsOn(id("research/scrap"))
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/cobblescrap"))
                        .nonogram(id("cobblescrap"))
                        .costs(5, 2, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("biomass_incubator"))
                        .icon(ModBlocks.biomassIncubator)
                        .dependsOn(id("research/biomass"))
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/biomass_incubator"))
                        .nonogram(id("biomass_incubator"))
                        .costs(5, 0, 2, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("entropic_data_miner"))
                        .icon(ModBlocks.entropicDataMiner)
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/entropic_data_miner"))
                        .nonogram(id("entropic_data_miner"))
                        .costs(0, 2, 2, 2)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("lavascrap"))
                        .icon(ModBlocks.lavascrap)
                        .dependsOn(id("research/cobblescrap"))
                        .unlocksRecipe(id("assembler/lavascrap"))
                        .nonogram(id("lavascrap"))
                        .costs(10, 32, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("biomass_harvester"))
                        .icon(ModBlocks.biomassHarvester)
                        .dependsOn(id("research/biomass_incubator"))
                        .unlocksRecipe(id("assembler/biomass_incubator"))
                        .nonogram(id("biomass_harvester"))
                        .costs(10, 0, 32, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("world_eater"))
                        .icon(ModBlocks.worldEater)
                        .dependsOn(id("research/lavascrap"))
                        .unlocksRecipe(id("assembler/world_eater"))
                        .nonogram(id("world_eater"))
                        .costs(20, 64, 0, 0)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("fragment_accelerator"))
                        .icon(ModBlocks.fragmentAccelerator)
                        .dependsOn(id("research/fragments"))
                        .dependsOn(id("research/assembler"))
                        .unlocksRecipe(id("assembler/fragment_accelerator"))
                        .nonogram(id("fragment_accelerator"))
                        .costs(20, 0, 0, 32)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("fragmental_waste"))
                        .icon(ModBlocks.fragmentalWaste)
                        .dependsOn(id("research/fragment_accelerator"))
                        .nonogram(id("fragmental_waste"))
                        .costs(1, 0, 0, 2)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("fragmental_generator"))
                        .icon(ModBlocks.fragmentalGenerator)
                        .dependsOn(id("research/fragment_accelerator"))
                        .unlocksRecipe(id("assembler/fragmental_generator"))
                        .nonogram(id("fragmental_generator"))
                        .costs(5, 0, 0, 2)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

                research(id("hazmat"))
                        .icon(ModItems.hazmatHelmet)
                        .dependsOn(id("research/fragmental_waste"))
                        .unlocksRecipe(id("hazmat_helmet"))
                        .unlocksRecipe(id("hazmat_chestplate"))
                        .unlocksRecipe(id("hazmat_leggings"))
                        .unlocksRecipe(id("hazmat_boots"))
                        .type(ResearchRecipe.Type.CRAFTING)
                        .nonogram(id("hazmat"))
                        .costs(1, 1, 4, 4)
                        .sortOrder(sortOrder += 100)
                        .save(output);

                research(id("chaos_engine"))
                        .icon(ModBlocks.chaosEngine)
                        .dependsOn(id("research/entropic_data_miner"))
                        .dependsOn(id("research/fragmental_waste"))
                        .unlocksRecipe(id("assembler/chaos_engine"))
                        .nonogram(id("chaos_engine"))
                        .costs(20, 24, 12, 48)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);

        /*research(id("replika_skin"))
                .icon(ModItems.replikaSkin)
                .dependsOn(id("research/burst_energy"))
                .nonogram(id("replika_skin"))
                .costs(8, 0, 12, 0)
                .sortOrder(sortOrder += 100)
                .save(output);*/

                research(id("replika_workbench"))
                        .icon(ModBlocks.replikaWorkbench)
                        .dependsOn(id("research/burst_energy"))
                        .unlocksRecipe(id("assembler/replika_workbench"))
                        .nonogram(id("replika_workbench"))
                        .costs(32, 32, 32, 32)
                        .sortOrder(sortOrder += 100)
                        .type(ResearchRecipe.Type.ASSEMBLER)
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Research Recipes";
    }

    private static ResearchRecipeBuilder research(Identifier id) {
        return new ResearchRecipeBuilder(id);
    }

    public static class ResearchRecipeBuilder {
        private final Identifier id;
        private ItemStackTemplate icon;
        private final List<Identifier> hardDependencies = new ArrayList<>();
        private final List<Identifier> softDependencies = new ArrayList<>();
        private final List<Identifier> unlockedRecipes = new ArrayList<>();
        private int scrap;
        private int biomass;
        private int fragments;
        private int data;
        private int sortOrder;
        private ResearchRecipe.Type type = ResearchRecipe.Type.LORE;
        private Identifier nonogram;

        private ResearchRecipeBuilder(Identifier id) {
            this.id = id;
        }

        public ResearchRecipeBuilder icon(ItemLike icon) {
            this.icon = new ItemStackTemplate(icon.asItem());
            return this;
        }

        public ResearchRecipeBuilder icon(ItemStackTemplate icon) {
            this.icon = icon;
            return this;
        }

        public ResearchRecipeBuilder unlocksRecipe(@Nullable Identifier recipeId) {
            if (recipeId != null) {
                this.unlockedRecipes.add(recipeId);
            }
            return this;
        }

        public ResearchRecipeBuilder costs(int data, int scrap, int biomass, int fragments) {
            this.data = data;
            this.scrap = scrap;
            this.biomass = biomass;
            this.fragments = fragments;
            return this;
        }

        public ResearchRecipeBuilder sortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public ResearchRecipeBuilder type(ResearchRecipe.Type type) {
            this.type = type;
            return this;
        }

        public ResearchRecipeBuilder dependsOn(Identifier id) {
            this.hardDependencies.add(id);
            return this;
        }

        public ResearchRecipeBuilder softDependsOn(Identifier id) {
            this.softDependencies.add(id);
            return this;
        }

        public ResearchRecipeBuilder nonogram(Identifier intro) {
            this.nonogram = intro;
            return this;
        }

        public void save(RecipeOutput output) {
            final var recipeId = id.withPrefix("research/");
            if (icon == null) {
                throw new IllegalStateException("Research recipe " + recipeId + " is missing an icon");
            }
            output.accept(ResourceKey.create(Registries.RECIPE, recipeId), new ResearchRecipe(icon, hardDependencies, softDependencies, unlockedRecipes, scrap, biomass, fragments, data, sortOrder, type, nonogram), null);
        }
    }
}
