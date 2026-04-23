package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record BiomassIncubatorRecipe(Ingredient seed, Ingredient soil, ItemStackTemplate result, float biomass)
        implements Recipe<SingleRecipeInput>, PreviewableRecipe {
    private static final MapCodec<BiomassIncubatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("seed").forGetter(BiomassIncubatorRecipe::seed),
            Ingredient.CODEC.fieldOf("soil").forGetter(BiomassIncubatorRecipe::soil),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(BiomassIncubatorRecipe::result),
            Codec.FLOAT.fieldOf("biomass").forGetter(BiomassIncubatorRecipe::biomass)
    ).apply(instance, BiomassIncubatorRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, BiomassIncubatorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            BiomassIncubatorRecipe::seed,
            Ingredient.CONTENTS_STREAM_CODEC,
            BiomassIncubatorRecipe::soil,
            ItemStackTemplate.STREAM_CODEC,
            BiomassIncubatorRecipe::result,
            ByteBufCodecs.FLOAT,
            BiomassIncubatorRecipe::biomass,
            BiomassIncubatorRecipe::new
    );

    public static Optional<BiomassIncubatorRecipe> getRecipe(Level level, ItemStack itemStack) {
        return level != null
                ? ModRecipes.biomassIncubator.getRecipeFor(level, new SingleRecipeInput(itemStack))
                .map(holder -> holder.value())
                : Optional.empty();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return seed.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return result.create();
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
        return result.create();
    }

    @Override
    public ItemStack previewResultItem() {
        return result.create();
    }

    @Override
    public RecipeSerializer<BiomassIncubatorRecipe> getSerializer() {
        return ModRecipes.biomassIncubator.serializer();
    }

    @Override
    public RecipeType<BiomassIncubatorRecipe> getType() {
        return ModRecipes.biomassIncubator.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(seed);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.biomassIncubator.bookCategory();
    }

    public static RecipeSerializer<BiomassIncubatorRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
