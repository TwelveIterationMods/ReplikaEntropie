package net.blay09.mods.replikaentropie.fabric.datagen;

import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Consumer;

public class ModFabricatorRecipeProvider extends FabricRecipeProvider {
    public ModFabricatorRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        int sortOrder = 0;
        fabricatorRecipe(new ItemStack(ModItems.biosteel), 2, 2, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(ModItems.chipset), 2, 0, 1, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(ModItems.hazmatLining), 4, 0, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(ModItems.replikaSkin), 8, 8, 2, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.CHAIN), 2, 0, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.STICK), 0, 1, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.BONE_MEAL), 0, 2, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.FLINT), 1, 0, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.SLIME_BALL), 0, 8, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.GUNPOWDER), 2, 0, 2, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.ICE), 0, 0, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.RAIL), 6, 1, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.BOWL), 1, 1, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.BRICK), 2, 0, 0, sortOrder += 100).save(exporter);
        fabricatorRecipe(new ItemStack(Items.PAPER), 0, 2, 0, sortOrder += 100).save(exporter);
    }

    private FabricatorRecipeBuilder fabricatorRecipe(ItemStack result, int scrap, int biomass, int fragments, int sortOrder) {
        return new FabricatorRecipeBuilder(result, scrap, biomass, fragments, sortOrder);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Fabricator Recipes";
    }

    public record FabricatorRecipeBuilder(ItemStack result, int scrap, int biomass, int fragments, int sortOrder) {
        public void save(Consumer<FinishedRecipe> exporter) {
            final var item = result.getItem();
            final var pathName = BuiltInRegistries.ITEM.getKey(item).getPath();
            final var id = new ResourceLocation(ReplikaEntropie.MOD_ID, "fabricator/" + pathName);
            exporter.accept(new FabricatorFinishedRecipe(id, result, scrap, biomass, fragments, sortOrder));
        }
    }

    public record FabricatorFinishedRecipe(ResourceLocation id, ItemStack result, int scrap, int biomass, int fragments, int sortOrder)
            implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            final var resultJson = new JsonObject();
            resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
            if (result.getCount() > 1) {
                resultJson.addProperty("count", result.getCount());
            }
            json.add("result", resultJson);
            json.addProperty("scrap", scrap);
            json.addProperty("biomass", biomass);
            json.addProperty("fragments", fragments);
            json.addProperty("sortOrder", sortOrder);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.fabricatorSerializer;
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
