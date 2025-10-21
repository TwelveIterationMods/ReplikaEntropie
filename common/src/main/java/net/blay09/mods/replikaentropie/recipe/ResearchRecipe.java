package net.blay09.mods.replikaentropie.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record ResearchRecipe(
        ResourceLocation id,
        ItemStack icon,
        List<ResourceLocation> hardDependencies,
        List<ResourceLocation> softDependencies,
        List<ResourceLocation> unlockedRecipes,
        int scrapCost,
        int biomassCost,
        int fragmentsCost,
        int dataCost,
        int sortOrder,
        Type type,
        ResourceLocation nonogram
) implements Recipe<Container> {

    public static Stream<ResourceLocation> getRecipeIds(Level level) {
        return level != null
                ? level.getRecipeManager().getAllRecipesFor(ModRecipes.researchType).stream().map(ResearchRecipe::id)
                : Stream.empty();
    }

    public enum Type {
        LORE,
        ASSEMBLER,
        FABRICATOR,
        CRAFTING
    }

    public static List<ResearchRecipe> getRecipes(Level level) {
        return level != null
                ? level.getRecipeManager().getAllRecipesFor(ModRecipes.researchType)
                : Collections.emptyList();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return icon;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.researchSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.researchType;
    }

    public static class Serializer implements RecipeSerializer<ResearchRecipe> {
        @Override
        public ResearchRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var icon = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "icon"));

            final var hardDependencies = new ArrayList<ResourceLocation>();
            if (json.has("hardDependencies")) {
                final var hardJson = GsonHelper.getAsJsonArray(json, "hardDependencies", new JsonArray());
                for (final var depJson : hardJson) {
                    hardDependencies.add(new ResourceLocation(GsonHelper.convertToString(depJson, "hardDependency")));
                }
            }

            final var softDependencies = new ArrayList<ResourceLocation>();
            if (json.has("softDependencies")) {
                final var softJson = GsonHelper.getAsJsonArray(json, "softDependencies", new JsonArray());
                for (final var depJson : softJson) {
                    softDependencies.add(new ResourceLocation(GsonHelper.convertToString(depJson, "softDependency")));
                }
            }

            final var unlockedRecipes = new ArrayList<ResourceLocation>();
            if (json.has("unlocked_recipes")) {
                final var recipesJson = GsonHelper.getAsJsonArray(json, "unlocked_recipes", new JsonArray());
                for (final var it : recipesJson) {
                    unlockedRecipes.add(new ResourceLocation(GsonHelper.convertToString(it, "unlocked_recipe")));
                }
            }

            final int scrap = GsonHelper.getAsInt(json, "scrap", 0);
            final int biomass = GsonHelper.getAsInt(json, "biomass", 0);
            final int fragments = GsonHelper.getAsInt(json, "fragments", 0);
            final int data = GsonHelper.getAsInt(json, "data", 0);
            final int sortOrder = GsonHelper.getAsInt(json, "sort_order", 0);
            final var type = json.has("research_type")
                    ? Type.valueOf(GsonHelper.getAsString(json, "research_type").toUpperCase())
                    : Type.LORE;
            
            final var nonogram = json.has("nonogram") 
                    ? new ResourceLocation(GsonHelper.getAsString(json, "nonogram")) 
                    : null;

            return new ResearchRecipe(id, icon, hardDependencies, softDependencies, unlockedRecipes, scrap, biomass, fragments, data, sortOrder, type, nonogram);
        }

        @Override
        public ResearchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var icon = buf.readItem();
            final var hardDependencies = buf.readList(FriendlyByteBuf::readResourceLocation);
            final var softDependencies = buf.readList(FriendlyByteBuf::readResourceLocation);
            final var unlockedRecipes = buf.readList(FriendlyByteBuf::readResourceLocation);
            final var scrap = buf.readVarInt();
            final var biomass = buf.readVarInt();
            final var fragments = buf.readVarInt();
            final var data = buf.readVarInt();
            final var sortOrder = buf.readVarInt();
            final var type = buf.readEnum(Type.class);
            final var nonogram = buf.readNullable(FriendlyByteBuf::readResourceLocation);
            return new ResearchRecipe(id, icon, hardDependencies, softDependencies, unlockedRecipes, scrap, biomass, fragments, data, sortOrder, type, nonogram);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ResearchRecipe recipe) {
            buf.writeItem(recipe.icon);
            buf.writeCollection(recipe.hardDependencies, FriendlyByteBuf::writeResourceLocation);
            buf.writeCollection(recipe.softDependencies, FriendlyByteBuf::writeResourceLocation);
            buf.writeCollection(recipe.unlockedRecipes, FriendlyByteBuf::writeResourceLocation);
            buf.writeVarInt(recipe.scrapCost);
            buf.writeVarInt(recipe.biomassCost);
            buf.writeVarInt(recipe.fragmentsCost);
            buf.writeVarInt(recipe.dataCost);
            buf.writeVarInt(recipe.sortOrder);
            buf.writeEnum(recipe.type);
            buf.writeNullable(recipe.nonogram, FriendlyByteBuf::writeResourceLocation);
        }
    }
}
