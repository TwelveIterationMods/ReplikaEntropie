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

public record FragmentAcceleratorRecipe(Ingredient ingredient, float speedMultiplier) implements Recipe<SingleRecipeInput>, PreviewableRecipe {
    private static final MapCodec<FragmentAcceleratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(FragmentAcceleratorRecipe::ingredient),
            Codec.FLOAT.optionalFieldOf("speed_multiplier", 1f).forGetter(FragmentAcceleratorRecipe::speedMultiplier)
    ).apply(instance, FragmentAcceleratorRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FragmentAcceleratorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            FragmentAcceleratorRecipe::ingredient,
            ByteBufCodecs.FLOAT,
            FragmentAcceleratorRecipe::speedMultiplier,
            FragmentAcceleratorRecipe::new
    );

    public static Optional<FragmentAcceleratorRecipe> getRecipe(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }

        return level != null
                ? ModRecipes.fragmentAccelerator.getRecipeFor(level, new SingleRecipeInput(itemStack)).map(RecipeHolder::value)
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
    public boolean isSpecial() {
        return true;
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
    public RecipeSerializer<FragmentAcceleratorRecipe> getSerializer() {
        return ModRecipes.fragmentAccelerator.serializer();
    }

    @Override
    public RecipeType<FragmentAcceleratorRecipe> getType() {
        return ModRecipes.fragmentAccelerator.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.fragmentAccelerator.bookCategory();
    }

    public static RecipeSerializer<FragmentAcceleratorRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
