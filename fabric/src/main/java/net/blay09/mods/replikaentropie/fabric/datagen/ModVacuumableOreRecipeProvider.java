package net.blay09.mods.replikaentropie.fabric.datagen;

import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModVacuumableOreRecipeProvider extends FabricRecipeProvider {
    public ModVacuumableOreRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        vacuumOre(Ingredient.of(Items.COAL_ORE), Blocks.STONE).save(exporter, "coal_ore");
        vacuumOre(Ingredient.of(Items.IRON_ORE), Blocks.STONE).save(exporter, "iron_ore");
        vacuumOre(Ingredient.of(Items.COPPER_ORE), Blocks.STONE).save(exporter, "copper_ore");
        vacuumOre(Ingredient.of(Items.GOLD_ORE), Blocks.STONE).save(exporter, "gold_ore");
        vacuumOre(Ingredient.of(Items.REDSTONE_ORE), Blocks.STONE).save(exporter, "redstone_ore");
        vacuumOre(Ingredient.of(Items.LAPIS_ORE), Blocks.STONE).save(exporter, "lapis_ore");
        vacuumOre(Ingredient.of(Items.DIAMOND_ORE), Blocks.STONE).save(exporter, "diamond_ore");
        vacuumOre(Ingredient.of(Items.EMERALD_ORE), Blocks.STONE).save(exporter, "emerald_ore");

        vacuumOre(Ingredient.of(Items.DEEPSLATE_COAL_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_coal_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_IRON_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_iron_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_COPPER_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_copper_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_GOLD_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_gold_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_REDSTONE_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_redstone_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_LAPIS_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_lapis_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_DIAMOND_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_diamond_ore");
        vacuumOre(Ingredient.of(Items.DEEPSLATE_EMERALD_ORE), Blocks.DEEPSLATE).save(exporter, "deepslate_emerald_ore");

        vacuumOre(Ingredient.of(Items.NETHER_QUARTZ_ORE), Blocks.NETHERRACK).save(exporter, "nether_quartz_ore");
        vacuumOre(Ingredient.of(Items.NETHER_GOLD_ORE), Blocks.NETHERRACK).save(exporter, "nether_gold_ore");

        vacuumOre(Ingredient.of(Items.ANCIENT_DEBRIS), Blocks.NETHERRACK).save(exporter, "ancient_debris");
    }

    private VacuumableOreRecipeBuilder vacuumOre(Ingredient ingredient, Block emptyBlock) {
        return new VacuumableOreRecipeBuilder(ingredient, emptyBlock);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Vacuumable Ore Recipes";
    }

    public record VacuumableOreRecipeBuilder(Ingredient ingredient, Block emptyBlock) {
        public void save(Consumer<FinishedRecipe> exporter, String name) {
            final var id = new ResourceLocation(ReplikaEntropie.MOD_ID, "vacuumable_ore/" + name);
            exporter.accept(new VacuumableOreFinishedRecipe(id, ingredient, emptyBlock));
        }
    }

    public record VacuumableOreFinishedRecipe(ResourceLocation id, Ingredient ingredient,
                                              Block emptyBlock) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", ingredient.toJson());
            json.addProperty("empty_block", BuiltInRegistries.BLOCK.getKey(emptyBlock).toString());
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.vacuumableOreSerializer;
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
