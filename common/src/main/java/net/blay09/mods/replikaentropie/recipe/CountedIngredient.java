package net.blay09.mods.replikaentropie.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record CountedIngredient(Ingredient ingredient, int count) {
    public static final Codec<CountedIngredient> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count)
            ).apply(instance, CountedIngredient::new)),
            Ingredient.CODEC.xmap(ingredient -> new CountedIngredient(ingredient, 1), CountedIngredient::ingredient)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CountedIngredient::ingredient,
            ByteBufCodecs.VAR_INT,
            CountedIngredient::count,
            CountedIngredient::new
    );

    public static CountedIngredient fromJson(JsonElement element) {
        return CODEC.parse(JsonOps.INSTANCE, element).getOrThrow();
    }

    public JsonObject toJson() {
        final var json = Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow().getAsJsonObject();
        if (count > 1) {
            json.addProperty("count", count);
        }
        return json;
    }
}
