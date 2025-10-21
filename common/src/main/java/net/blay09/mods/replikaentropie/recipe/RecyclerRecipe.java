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
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RecyclerRecipe(ResourceLocation id, Ingredient ingredient, float scrap, float biomass,
                             float fragments) implements Recipe<Container> {
    public static Optional<RecyclerRecipe> getRecipe(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }

        return level != null
                ? level.getRecipeManager().getRecipeFor(ModRecipes.recyclerType, new SingleItemContainer(itemStack), level)
                : Optional.empty();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.recyclerSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.recyclerType;
    }

    public static class Serializer implements RecipeSerializer<RecyclerRecipe> {
        @Override
        public RecyclerRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            final var ingredient = Ingredient.fromJson(jsonObject.get("ingredient"));
            final var scrap = jsonObject.get("scrap").getAsFloat();
            final var biomass = jsonObject.get("biomass").getAsFloat();
            final var fragments = jsonObject.get("fragments").getAsFloat();
            return new RecyclerRecipe(id, ingredient, scrap, biomass, fragments);
        }

        @Override
        public RecyclerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredient = Ingredient.fromNetwork(buf);
            final var scrap = buf.readFloat();
            final var biomass = buf.readFloat();
            final var fragments = buf.readFloat();
            return new RecyclerRecipe(id, ingredient, scrap, biomass, fragments);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RecyclerRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeFloat(recipe.scrap);
            buf.writeFloat(recipe.biomass);
            buf.writeFloat(recipe.fragments);
        }
    }
}
