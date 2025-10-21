package net.blay09.mods.replikaentropie.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModResearchRecipeProvider extends FabricRecipeProvider {
    public ModResearchRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        int sortOrder = 0;
        research(id("sky_scraper"))
                .icon(ModItems.skyScraper)
                .nonogram(id("sky_scraper"))
                .unlocksRecipe(id("handheld_analyzer"))
                .type(ResearchRecipe.Type.CRAFTING)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("handheld_analyzer"))
                .icon(ModItems.handheldAnalyzer)
                .dependsOn(id("research/sky_scraper"))
                .nonogram(id("handheld_analyzer"))
                .costs(1, 0, 0, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("recycler"))
                .icon(ModBlocks.recycler)
                .dependsOn(id("research/handheld_analyzer"))
                .unlocksRecipe(id("recycler"))
                .type(ResearchRecipe.Type.CRAFTING)
                .nonogram(id("recycler"))
                .costs(5, 0, 0, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("entropic_energy"))
                .icon(Items.DAYLIGHT_DETECTOR)
                .dependsOn(id("research/recycler"))
                .nonogram(id("entropic_energy"))
                .costs(1, 0, 0, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("scrap"))
                .icon(ModItems.scrap)
                .dependsOn(id("research/recycler"))
                .nonogram(id("scrap"))
                .costs(1, 1, 0, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("biomass"))
                .icon(ModItems.biomass)
                .dependsOn(id("research/recycler"))
                .nonogram(id("biomass"))
                .costs(1, 0, 1, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("fragments"))
                .icon(ModItems.fragments)
                .dependsOn(id("research/recycler"))
                .nonogram(id("fragments"))
                .costs(1, 0, 0, 1)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("biosteel"))
                .icon(ModItems.biosteel)
                .dependsOn(id("research/scrap"))
                .dependsOn(id("research/biomass"))
                .nonogram(id("biosteel"))
                .costs(1, 1, 1, 0)
                .type(ResearchRecipe.Type.CRAFTING)
                .sortOrder(sortOrder += 100)
                .save(exporter);

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
                .save(exporter);

        research(id("assembly_ticket"))
                .icon(ModItems.assemblyTicket)
                .dependsOn(id("research/assembler"))
                .nonogram(id("assembly_ticket"))
                .costs(1, 0, 0, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("fabricator"))
                .icon(ModBlocks.assembler)
                .dependsOn(id("research/assembler"))
                .unlocksRecipe(id("assembler/fabricator"))
                .nonogram(id("fabricator"))
                .costs(5, 2, 2, 2)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("burst_energy"))
                .icon(Items.LIGHTNING_ROD)
                .dependsOn(id("research/assembler"))
                .nonogram(id("burst_energy"))
                .costs(5, 12, 12, 6)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("burst_drill"))
                .icon(ModItems.burstDrill)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/burst_drill"))
                .nonogram(id("burst_drill"))
                .costs(8, 12, 0, 1)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("ore_vacuum"))
                .icon(ModItems.oreVacuum)
                .dependsOn(id("research/burst_drill"))
                .unlocksRecipe(id("assembler/ore_vacuum"))
                .nonogram(id("ore_vacuum"))
                .costs(10, 8, 0, 2)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("slowphasers"))
                .icon(ModItems.slowphasers)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/slowphasers"))
                .nonogram(id("slowphasers"))
                .costs(10, 4, 0, 4)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("magphasers"))
                .icon(ModItems.magphasers)
                .dependsOn(id("research/slowphasers"))
                .unlocksRecipe(id("assembler/magphasers"))
                .nonogram(id("magphasers"))
                .costs(20, 4, 0, 8)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("nullphaser"))
                .icon(ModItems.nullphaser)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/nullphaser"))
                .nonogram(id("nullphaser"))
                .costs(8, 4, 0, 8)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("semisonic_speeders"))
                .icon(ModItems.semisonicSpeeders)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/semisonic_speeders"))
                .nonogram(id("semisonic_speeders"))
                .costs(12, 8, 4, 2)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("spring_boots"))
                .icon(ModItems.springBoots)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/spring_boots"))
                .nonogram(id("spring_boots"))
                .costs(12, 8, 2, 0)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("stompers"))
                .icon(ModItems.stompers)
                .dependsOn(id("research/spring_boots"))
                .unlocksRecipe(id("assembler/stompers"))
                .nonogram(id("stompers"))
                .costs(24, 8, 0, 2)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("nightvision_goggles"))
                .icon(ModItems.nightVisionGoggles)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/nightvision_goggles"))
                .nonogram(id("nightvision_goggles"))
                .costs(12, 4, 0, 4)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("brightvision_goggles"))
                .icon(ModItems.brightVisionGoggles)
                .dependsOn(id("research/nightvision_goggles"))
                .unlocksRecipe(id("assembler/brightvision_goggles"))
                .nonogram(id("brightvision_goggles"))
                .costs(24, 8, 8, 16)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("gravilift_harness"))
                .icon(ModItems.graviliftHarness)
                .dependsOn(id("research/burst_energy"))
                .unlocksRecipe(id("assembler/gravilift_harness"))
                .nonogram(id("gravilift_harness"))
                .costs(32, 16, 4, 32)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("cobblescrap"))
                .icon(ModBlocks.cobblescrap)
                .dependsOn(id("research/scrap"))
                .dependsOn(id("research/assembler"))
                .unlocksRecipe(id("assembler/cobblescrap"))
                .nonogram(id("cobblescrap"))
                .costs(5, 2, 0, 0)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("biomass_incubator"))
                .icon(ModBlocks.biomassIncubator)
                .dependsOn(id("research/biomass"))
                .dependsOn(id("research/assembler"))
                .unlocksRecipe(id("assembler/biomass_incubator"))
                .nonogram(id("biomass_incubator"))
                .costs(5, 0, 2, 0)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("defragmentizer"))
                .icon(ModBlocks.defragmentizer)
                .dependsOn(id("research/fragments"))
                .dependsOn(id("research/assembler"))
                .unlocksRecipe(id("assembler/defragmentizer"))
                .nonogram(id("defragmentizer"))
                .costs(5, 0, 0, 2)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("entropic_data_miner"))
                .icon(ModBlocks.entropicDataMiner)
                .dependsOn(id("research/assembler"))
                .unlocksRecipe(id("assembler/entropic_data_miner"))
                .nonogram(id("entropic_data_miner"))
                .costs(0, 2, 2, 2)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("lavascrap"))
                .icon(ModBlocks.lavascrap)
                .dependsOn(id("research/cobblescrap"))
                .unlocksRecipe(id("assembler/lavascrap"))
                .nonogram(id("lavascrap"))
                .costs(10, 32, 0, 0)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("biomass_harvester"))
                .icon(ModBlocks.biomassHarvester)
                .dependsOn(id("research/biomass_incubator"))
                .unlocksRecipe(id("assembler/biomass_incubator"))
                .nonogram(id("biomass_harvester"))
                .costs(10, 0, 32, 0)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("world_eater"))
                .icon(ModBlocks.worldEater)
                .dependsOn(id("research/lavascrap"))
                .unlocksRecipe(id("assembler/world_eater"))
                .nonogram(id("world_eater"))
                .costs(20, 64, 0, 0)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("fragmental_waste"))
                .icon(ModBlocks.fragmentalWaste)
                .dependsOn(id("research/defragmentizer"))
                .nonogram(id("fragmental_waste"))
                .costs(1, 0, 0, 2)
                .sortOrder(sortOrder += 100)
                .save(exporter);

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
                .save(exporter);

        research(id("fragment_accelerator"))
                .icon(ModBlocks.fragmentAccelerator)
                .dependsOn(id("research/fragmental_waste"))
                .unlocksRecipe(id("assembler/fragment_accelerator"))
                .nonogram(id("fragment_accelerator"))
                .costs(20, 0, 0, 32)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("chaos_engine"))
                .icon(ModBlocks.chaosEngine)
                .dependsOn(id("research/entropic_data_miner"))
                .dependsOn(id("research/fragmental_waste"))
                .unlocksRecipe(id("assembler/chaos_engine"))
                .nonogram(id("chaos_engine"))
                .costs(20, 24, 12, 48)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);

        research(id("replika_skin"))
                .icon(ModItems.replikaSkin)
                .dependsOn(id("research/burst_energy"))
                .nonogram(id("replika_skin"))
                .costs(8, 0, 12, 0)
                .sortOrder(sortOrder += 100)
                .save(exporter);

        research(id("replika_workbench"))
                .icon(ModBlocks.replikaWorkbench)
                .dependsOn(id("research/replika_skin"))
                .unlocksRecipe(id("assembler/replika_workbench"))
                .nonogram(id("replika_workbench"))
                .costs(32, 32, 32, 32)
                .sortOrder(sortOrder += 100)
                .type(ResearchRecipe.Type.ASSEMBLER)
                .save(exporter);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Research Recipes";
    }

    private static ResearchRecipeBuilder research(ResourceLocation id) {
        return new ResearchRecipeBuilder(id);
    }

    public static class ResearchRecipeBuilder {
        private final ResourceLocation id;
        private ItemStack icon = ItemStack.EMPTY;
        private final List<ResourceLocation> hardDependencies = new ArrayList<>();
        private final List<ResourceLocation> softDependencies = new ArrayList<>();
        private final List<ResourceLocation> unlockedRecipes = new ArrayList<>();
        private int scrap;
        private int biomass;
        private int fragments;
        private int data;
        private int sortOrder;
        private ResearchRecipe.Type type = ResearchRecipe.Type.LORE;
        private ResourceLocation nonogram;

        private ResearchRecipeBuilder(ResourceLocation id) {
            this.id = id;
        }

        public ResearchRecipeBuilder icon(ItemLike icon) {
            this.icon = new ItemStack(icon);
            return this;
        }

        public ResearchRecipeBuilder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        public ResearchRecipeBuilder unlocksRecipe(@Nullable ResourceLocation recipeId) {
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

        public ResearchRecipeBuilder dependsOn(ResourceLocation id) {
            this.hardDependencies.add(id);
            return this;
        }

        public ResearchRecipeBuilder softDependsOn(ResourceLocation id) {
            this.softDependencies.add(id);
            return this;
        }

        public ResearchRecipeBuilder nonogram(ResourceLocation intro) {
            this.nonogram = intro;
            return this;
        }

        public void save(Consumer<FinishedRecipe> exporter) {
            exporter.accept(new ResearchFinishedRecipe(id, icon, hardDependencies, softDependencies, unlockedRecipes, scrap, biomass, fragments, data, sortOrder, type, nonogram));
        }
    }

    public record ResearchFinishedRecipe(
            ResourceLocation id,
            ItemStack icon,
            List<ResourceLocation> hardDependencies,
            List<ResourceLocation> softDependencies,
            List<ResourceLocation> unlockedRecipes,
            int scrap,
            int biomass,
            int fragments,
            int data,
            int sortOrder,
            ResearchRecipe.Type type,
            ResourceLocation nonogram
    ) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            final var iconJson = new JsonObject();
            iconJson.addProperty("item", BuiltInRegistries.ITEM.getKey(icon.getItem()).toString());
            json.add("icon", iconJson);

            final var hardJson = new JsonArray();
            for (final var dependency : hardDependencies) {
                hardJson.add(dependency.toString());
            }
            json.add("hardDependencies", hardJson);

            final var softJson = new JsonArray();
            for (final var dependency : softDependencies) {
                softJson.add(dependency.toString());
            }
            json.add("softDependencies", softJson);

            if (!unlockedRecipes.isEmpty()) {
                final var arr = new JsonArray();
                for (final var r : unlockedRecipes) {
                    arr.add(r.toString());
                }
                json.add("unlocked_recipes", arr);
            }
            json.addProperty("scrap", scrap);
            json.addProperty("biomass", biomass);
            json.addProperty("fragments", fragments);
            json.addProperty("data", data);
            json.addProperty("sort_order", sortOrder);
            json.addProperty("research_type", type.name().toLowerCase());

            if (nonogram != null) {
                json.addProperty("nonogram", nonogram.toString());
            }
        }

        @Override
        public ResourceLocation getId() {
            return id.withPrefix("research/");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.researchSerializer;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
