package net.blay09.mods.replikaentropie.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.CountedIngredient;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.util.function.Consumer;

public class ModAssemblerRecipeProvider extends FabricRecipeProvider {
    public ModAssemblerRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        assemblerRecipe(new ItemStack(ModBlocks.fabricator), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 1)
                .ingredient(Items.REDSTONE, 8)
                .ingredient(ModItems.scrap, 8)
                .ingredient(ModItems.fragments, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.replikaWorkbench), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.DIAMOND, 4)
                .ingredient(ModItems.chipset, 4)
                .ingredient(ModItems.scrap, 8)
                .ingredient(ModItems.fragments, 4)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.entropicDataMiner), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(ModItems.chipset, 2)
                .ingredient(Items.COPPER_INGOT, 3)
                .ingredient(ModItems.fragments, 8)
                .ingredient(ModItems.biomass, 8)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.nightVisionGoggles), 1)
                .ingredient(Items.IRON_INGOT, 2)
                .ingredient(Items.GLOWSTONE_DUST, 2)
                .ingredient(Items.GREEN_DYE, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.brightVisionGoggles), 1)
                .ingredient(Items.IRON_INGOT, 2)
                .ingredient(Items.GLOWSTONE_DUST, 2)
                .ingredient(Items.GLOW_INK_SAC, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.graviliftHarness), 1)
                .ingredient(Items.IRON_INGOT, 8)
                .ingredient(Items.FEATHER, 8)
                .ingredient(Items.GHAST_TEAR, 2)
                .ingredient(Items.NETHER_STAR, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.chipset), 1)
                .ingredient(Items.REDSTONE, 2)
                .ingredient(Items.IRON_INGOT, 1)
                .ingredient(Items.GREEN_DYE, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.biomassIncubator), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(ModItems.biomass, 4)
                .ingredient(Items.GLASS, 4)
                .ingredient(Items.DIRT, 3)
                .ingredient(Items.WATER_BUCKET, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.biomassHarvester), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.RAIL, 8)
                .ingredient(Items.STICK, 4)
                .ingredient(Items.HOPPER, 1)
                .ingredient(Items.LEAD, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.cobblescrap), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 6)
                .ingredient(Items.FLINT, 3)
                .ingredient(Items.WATER_BUCKET, 1)
                .ingredient(Items.LAVA_BUCKET, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.lavascrap), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 6)
                .ingredient(Items.BRICK, 4)
                .ingredient(Items.DIAMOND, 3)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.worldEater), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.DIAMOND, 3)
                .ingredient(Items.GLASS, 2)
                .ingredient(Items.SPYGLASS, 1)
                .ingredient(Items.FISHING_ROD, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.defragmentizer), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.GLASS, 8)
                .ingredient(Items.GUNPOWDER, 4)
                .ingredient(Items.BLAZE_POWDER, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.fragmentAccelerator), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.POWERED_RAIL, 8)
                .ingredient(Items.COPPER_INGOT, 3)
                .ingredient(Items.REDSTONE_TORCH, 1)
                .ingredient(Items.CLOCK, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModBlocks.chaosEngine), 1)
                .ingredient(Items.IRON_INGOT, 9)
                .ingredient(Items.NETHER_STAR, 1)
                .ingredient(Items.RECOVERY_COMPASS, 1)
                .ingredient(Items.CLOCK, 1)
                .ingredient(Items.COMPASS, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.nullphaser), 1)
                .ingredient(Items.IRON_INGOT, 2)
                .ingredient(Items.ENDER_PEARL, 1)
                .ingredient(Items.GLASS, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.burstDrill), 1)
                .ingredient(Items.IRON_INGOT, 5)
                .ingredient(Items.DIAMOND, 1)
                .ingredient(Items.STICK, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.oreVacuum), 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.HOPPER, 1)
                .ingredient(Items.STICK, 1)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.slowphasers), 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.ICE, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.magphasers), 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.ECHO_SHARD, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.stompers), 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.OBSIDIAN, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.springBoots), 1)
                .ingredient(Items.IRON_INGOT, 4)
                .ingredient(Items.STRING, 2)
                .ingredient(Items.CHAIN, 2)
                .save(exporter);

        assemblerRecipe(new ItemStack(ModItems.semisonicSpeeders), 1)
                .ingredient(Items.IRON_INGOT, 7)
                .ingredient(Items.BLAZE_ROD, 2)
                .save(exporter);
    }

    public static AssemblerRecipeBuilder assemblerRecipe(ItemStack result, int count) {
        return new AssemblerRecipeBuilder(result, count);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Assembler Recipes";
    }

    public static class AssemblerRecipeBuilder {
        private final ItemStack result;
        private final int count;
        private final List<CountedIngredient> ingredients = new java.util.ArrayList<>();

        public AssemblerRecipeBuilder(ItemStack result, int count) {
            this.result = result;
            this.count = count;
        }

        public AssemblerRecipeBuilder ingredient(Item item, int count) {
            return ingredient(Ingredient.of(item), count);
        }

        public AssemblerRecipeBuilder ingredient(TagKey<Item> tag, int count) {
            return ingredient(Ingredient.of(tag), count);
        }

        public AssemblerRecipeBuilder ingredient(Ingredient ingredient, int count) {
            this.ingredients.add(new CountedIngredient(ingredient, count));
            return this;
        }

        public void save(Consumer<FinishedRecipe> exporter) {
            final var item = result.getItem();
            final var pathName = BuiltInRegistries.ITEM.getKey(item).getPath();
            final var id = new ResourceLocation(ReplikaEntropie.MOD_ID, "assembler/" + pathName + (count > 1 ? "_" + count : ""));
            
            exporter.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(JsonObject json) {
                    final var ingredientsJson = new JsonArray();
                    for (final var ingredient : ingredients) {
                        ingredientsJson.add(ingredient.toJson());
                    }
                    json.add("ingredients", ingredientsJson);

                    final var resultJson = new JsonObject();
                    resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
                    if (count > 1) {
                        resultJson.addProperty("count", count);
                    }
                    json.add("result", resultJson);
                }

                @Override
                public ResourceLocation getId() {
                    return id;
                }

                @Override
                public RecipeSerializer<?> getType() {
                    return ModRecipes.assemblerSerializer;
                }

                @Override
                public JsonObject serializeAdvancement() {
                    return null;
                }

                @Override
                public ResourceLocation getAdvancementId() {
                    return null;
                }
            });
        }
    }
}
