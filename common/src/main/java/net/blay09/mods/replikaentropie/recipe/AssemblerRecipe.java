package net.blay09.mods.replikaentropie.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record AssemblerRecipe(ResourceLocation id,
                              List<CountedIngredient> ingredients,
                              ItemStack result) implements Recipe<Container> {

    @Override
    public boolean matches(Container container, Level level) {
        final var inputStacks = new ArrayList<ItemStack>();
        final var remainingCounts = new ArrayList<Integer>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            final var stack = container.getItem(i);
            if (!stack.isEmpty()) {
                inputStacks.add(stack);
                remainingCounts.add(stack.getCount());
            }
        }

        for (final var countedIngredient : ingredients()) {
            var needed = Math.max(1, countedIngredient.count());
            final var ingredient = countedIngredient.ingredient();
            for (int i = 0; i < inputStacks.size() && needed > 0; i++) {
                if (ingredient.test(inputStacks.get(i))) {
                    final var toTake = Math.min(remainingCounts.get(i), needed);
                    if (toTake > 0) {
                        remainingCounts.set(i, remainingCounts.get(i) - toTake);
                        needed -= toTake;
                    }
                }
            }
            if (needed > 0) {
                return false;
            }
        }

        return true;
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
        return ModRecipes.assemblerSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.assemblerType;
    }

    public static class Serializer implements RecipeSerializer<AssemblerRecipe> {
        @Override
        public AssemblerRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var ingredientsJson = json.getAsJsonArray("ingredients");
            if (ingredientsJson == null || ingredientsJson.isEmpty() || ingredientsJson.size() > 5) {
                throw new IllegalArgumentException("Assembler recipe must have between 1 and 5 ingredients");
            }
            final var countedIngredients = new ArrayList<CountedIngredient>(ingredientsJson.size());
            for (int i = 0; i < ingredientsJson.size(); i++) {
                countedIngredients.add(CountedIngredient.fromJson(ingredientsJson.get(i)));
            }
            final var result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
            return new AssemblerRecipe(id, countedIngredients, result);
        }

        @Override
        public AssemblerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredients = buf.readList((it) -> {
                final var ing = Ingredient.fromNetwork(it);
                final int count = it.readVarInt();
                return new CountedIngredient(ing, count);
            });
            final var result = buf.readItem();
            return new AssemblerRecipe(id, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AssemblerRecipe recipe) {
            buf.writeCollection(recipe.ingredients(), (it, item) -> {
                item.ingredient().toNetwork(it);
                it.writeVarInt(Math.max(1, item.count()));
            });
            buf.writeItem(recipe.result());
        }
    }

}
