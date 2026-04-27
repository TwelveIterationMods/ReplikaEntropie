package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.mixin.RecipeManagerAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public record FabricatorRecipe(int scrap, int biomass, int fragments,
                               ItemStackTemplate result,
                               int sortOrder) implements Recipe<RecipeInput>, PreviewableRecipe {
    private static final MapCodec<FabricatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("scrap", 0).forGetter(FabricatorRecipe::scrap),
            Codec.INT.optionalFieldOf("biomass", 0).forGetter(FabricatorRecipe::biomass),
            Codec.INT.optionalFieldOf("fragments", 0).forGetter(FabricatorRecipe::fragments),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(FabricatorRecipe::result),
            Codec.INT.optionalFieldOf("sortOrder", 0).forGetter(FabricatorRecipe::sortOrder)
    ).apply(instance, FabricatorRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FabricatorRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            FabricatorRecipe::scrap,
            ByteBufCodecs.VAR_INT,
            FabricatorRecipe::biomass,
            ByteBufCodecs.VAR_INT,
            FabricatorRecipe::fragments,
            ItemStackTemplate.STREAM_CODEC,
            FabricatorRecipe::result,
            ByteBufCodecs.VAR_INT,
            FabricatorRecipe::sortOrder,
            FabricatorRecipe::new
    );

    public static List<RecipeHolder<FabricatorRecipe>> getRecipes(Level level) {
        return level instanceof ServerLevel serverLevel
                ? ((RecipeManagerAccessor) serverLevel.recipeAccess()).balm$getRecipeMap().byType(ModRecipes.fabricator.type()).stream()
                  .sorted(Comparator.comparingInt(it -> it.value().sortOrder()))
                  .toList()
                : Collections.emptyList();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return result.create();
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
        return result.create();
    }

    @Override
    public RecipeSerializer<FabricatorRecipe> getSerializer() {
        return ModRecipes.fabricator.serializer();
    }

    @Override
    public RecipeType<FabricatorRecipe> getType() {
        return ModRecipes.fabricator.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.fabricator.bookCategory();
    }

    public static RecipeSerializer<FabricatorRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
