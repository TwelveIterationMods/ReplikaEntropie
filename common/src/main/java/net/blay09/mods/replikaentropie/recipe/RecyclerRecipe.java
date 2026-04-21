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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RecyclerRecipe(Ingredient ingredient, float scrap, float biomass,
                             float fragments) implements Recipe<SingleRecipeInput>, PreviewableRecipe {
    private static final MapCodec<RecyclerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(RecyclerRecipe::ingredient),
            Codec.FLOAT.fieldOf("scrap").forGetter(RecyclerRecipe::scrap),
            Codec.FLOAT.fieldOf("biomass").forGetter(RecyclerRecipe::biomass),
            Codec.FLOAT.fieldOf("fragments").forGetter(RecyclerRecipe::fragments)
    ).apply(instance, RecyclerRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, RecyclerRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            RecyclerRecipe::ingredient,
            ByteBufCodecs.FLOAT,
            RecyclerRecipe::scrap,
            ByteBufCodecs.FLOAT,
            RecyclerRecipe::biomass,
            ByteBufCodecs.FLOAT,
            RecyclerRecipe::fragments,
            RecyclerRecipe::new
    );

    public static Optional<RecyclerRecipe> getRecipe(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }

        return level != null
                ? ModRecipes.recycler.getRecipeFor(level, new SingleRecipeInput(itemStack))
                .map(holder -> holder.value())
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

    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack previewResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<RecyclerRecipe> getSerializer() {
        return ModRecipes.recycler.serializer();
    }

    @Override
    public RecipeType<RecyclerRecipe> getType() {
        return ModRecipes.recycler.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.recycler.bookCategory();
    }

    public static RecipeSerializer<RecyclerRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
