package net.blay09.mods.replikaentropie.recipe;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public record FabricatorRecipe(ResourceLocation id, int scrap, int biomass, int fragments,
                               ItemStack result, int sortOrder) implements Recipe<Container> {

    public static List<FabricatorRecipe> getRecipes(Level level) {
        return level != null
                ? level.getRecipeManager().getAllRecipesFor(ModRecipes.fabricatorType).stream()
                .sorted(Comparator.comparingInt(FabricatorRecipe::sortOrder).thenComparing(FabricatorRecipe::getId))
                .toList()
                : Collections.emptyList();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.fabricatorSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.fabricatorType;
    }

    public static class Serializer implements RecipeSerializer<FabricatorRecipe> {
        @Override
        public FabricatorRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            final var scrap = GsonHelper.getAsInt(jsonObject, "scrap", 0);
            final var biomass = GsonHelper.getAsInt(jsonObject, "biomass", 0);
            final var fragments = GsonHelper.getAsInt(jsonObject, "fragments", 0);
            final var result = ShapedRecipe.itemStackFromJson(jsonObject.getAsJsonObject("result"));
            final var sortOrder = GsonHelper.getAsInt(jsonObject, "sortOrder", 0);
            return new FabricatorRecipe(id, scrap, biomass, fragments, result, sortOrder);
        }

        @Override
        public FabricatorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var scrap = buf.readVarInt();
            final var biomass = buf.readVarInt();
            final var fragments = buf.readVarInt();
            final var result = buf.readItem();
            final var sortOrder = buf.readVarInt();
            return new FabricatorRecipe(id, scrap, biomass, fragments, result, sortOrder);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FabricatorRecipe recipe) {
            buf.writeVarInt(recipe.scrap);
            buf.writeVarInt(recipe.biomass);
            buf.writeVarInt(recipe.fragments);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.sortOrder);
        }
    }
}
