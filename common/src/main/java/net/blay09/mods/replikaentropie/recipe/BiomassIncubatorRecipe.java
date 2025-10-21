package net.blay09.mods.replikaentropie.recipe;

import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.container.SingleItemContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record BiomassIncubatorRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, float biomass)
        implements Recipe<Container> {

    public static Optional<BiomassIncubatorRecipe> getRecipe(Level level, ItemStack itemStack) {
        return level != null
                ? level.getRecipeManager().getRecipeFor(ModRecipes.biomassIncubatorType, new SingleItemContainer(itemStack), level)
                : Optional.empty();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
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
        return ModRecipes.biomassIncubatorSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.biomassIncubatorType;
    }

    public static class Serializer implements RecipeSerializer<BiomassIncubatorRecipe> {
        @Override
        public BiomassIncubatorRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var ingredient = Ingredient.fromJson(json.get("ingredient"));
            final var result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
            final var biomass = json.get("biomass").getAsFloat();
            return new BiomassIncubatorRecipe(id, ingredient, result, biomass);
        }

        @Override
        public BiomassIncubatorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredient = Ingredient.fromNetwork(buf);
            final var result = buf.readItem();
            final var biomass = buf.readFloat();
            return new BiomassIncubatorRecipe(id, ingredient, result, biomass);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BiomassIncubatorRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeFloat(recipe.biomass);
        }
    }
}
