package net.blay09.mods.replikaentropie.fabric.datagen;

import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ModBiomassIncubatorRecipeProvider extends FabricRecipeProvider {
    public ModBiomassIncubatorRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        incubatorRecipe(Items.WHEAT_SEEDS, new ItemStack(Items.WHEAT), 0.2f).save(exporter);
        incubatorRecipe(Items.BEETROOT_SEEDS, new ItemStack(Items.BEETROOT), 0.2f).save(exporter);
        incubatorRecipe(Items.PUMPKIN_SEEDS, new ItemStack(Items.PUMPKIN), 0.2f).save(exporter);
        incubatorRecipe(Items.MELON_SEEDS, new ItemStack(Items.MELON), 0.2f).save(exporter);
        incubatorRecipe(Items.TORCHFLOWER_SEEDS, new ItemStack(Items.TORCHFLOWER), 0.2f).save(exporter);
        incubatorRecipe(Items.CARROT, new ItemStack(Items.CARROT), 0.2f).save(exporter);
        incubatorRecipe(Items.POTATO, new ItemStack(Items.POTATO), 0.2f).save(exporter);
    }

    private BiomassIncubatorRecipeBuilder incubatorRecipe(Item input, ItemStack output, float biomass) {
        return new BiomassIncubatorRecipeBuilder(Ingredient.of(input), output, biomass);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Biomass Incubator Recipes";
    }

    public record BiomassIncubatorRecipeBuilder(Ingredient ingredient, ItemStack result, float biomass) {
        public void save(Consumer<FinishedRecipe> exporter) {
            final var pathName = ingredient.getItems()[0].getItem().toString().replace("minecraft:", "");
            final var id = new ResourceLocation(ReplikaEntropie.MOD_ID, "biomass_incubator/" + pathName);
            exporter.accept(new BiomassIncubatorFinishedRecipe(id, ingredient, result, biomass));
        }
    }

    public record BiomassIncubatorFinishedRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, float biomass)
            implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", ingredient.toJson());
            final var resultJson = new JsonObject();
            resultJson.addProperty("item", result.getItem().toString());
            if (result.getCount() > 1) {
                resultJson.addProperty("count", result.getCount());
            }
            json.add("result", resultJson);
            json.addProperty("biomass", biomass);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.biomassIncubatorSerializer;
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
