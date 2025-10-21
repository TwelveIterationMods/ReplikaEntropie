package net.blay09.mods.replikaentropie.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.Ingredient;

public record CountedIngredient(Ingredient ingredient, int count) {
    public static CountedIngredient fromJson(JsonElement element) {
        if (element.isJsonObject()) {
            final var jsonObject = element.getAsJsonObject();
            if (jsonObject.has("ingredient") || jsonObject.has("count")) {
                final var ingredientJson = jsonObject.has("ingredient") ? jsonObject.get("ingredient") : element;
                final var ingredient = Ingredient.fromJson(ingredientJson);
                final int count = jsonObject.has("count") ? Math.max(1, jsonObject.get("count").getAsInt()) : 1;
                return new CountedIngredient(ingredient, count);
            }
        }
        return new CountedIngredient(Ingredient.fromJson(element), 1);
    }

    public JsonObject toJson() {
        final var json = ingredient.toJson().getAsJsonObject();
        if (count > 1) {
            json.addProperty("count", count);
        }
        return json;
    }
}
