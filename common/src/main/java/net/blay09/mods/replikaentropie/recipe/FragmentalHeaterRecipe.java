package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record FragmentalHeaterRecipe(Ingredient ingredient, int energy, float temperature) implements Recipe<SingleRecipeInput>, PreviewableRecipe {
    private static final MapCodec<FragmentalHeaterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(FragmentalHeaterRecipe::ingredient),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(FragmentalHeaterRecipe::energy),
            Codec.FLOAT.optionalFieldOf("temperature", 0f).forGetter(FragmentalHeaterRecipe::temperature)
    ).apply(instance, FragmentalHeaterRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FragmentalHeaterRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            FragmentalHeaterRecipe::ingredient,
            ByteBufCodecs.VAR_INT,
            FragmentalHeaterRecipe::energy,
            ByteBufCodecs.FLOAT,
            FragmentalHeaterRecipe::temperature,
            FragmentalHeaterRecipe::new
    );

    public static Optional<FragmentalHeaterRecipe> getRecipe(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }

        return level != null
                ? ModRecipes.fragmentalHeater.getRecipeFor(level, new SingleRecipeInput(itemStack)).map(RecipeHolder::value)
                : Optional.empty();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public ItemStack previewResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<FragmentalHeaterRecipe> getSerializer() {
        return ModRecipes.fragmentalHeater.serializer();
    }

    @Override
    public RecipeType<FragmentalHeaterRecipe> getType() {
        return ModRecipes.fragmentalHeater.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.fragmentalHeater.bookCategory();
    }

    public static RecipeSerializer<FragmentalHeaterRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
