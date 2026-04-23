package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

// POSTJAM Should we make this use BlockState instead of Ingredient?
public record VacuumableOreRecipe(Ingredient ingredient, Block emptyBlock) implements Recipe<SingleRecipeInput>, PreviewableRecipe {
    private static final MapCodec<VacuumableOreRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(VacuumableOreRecipe::ingredient),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("empty_block").forGetter(VacuumableOreRecipe::emptyBlock)
    ).apply(instance, VacuumableOreRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, VacuumableOreRecipe> STREAM_CODEC = StreamCodec.of(
            VacuumableOreRecipe::toNetwork,
            VacuumableOreRecipe::fromNetwork
    );

    public static Optional<VacuumableOreRecipe> getRecipe(Level level, BlockState state) {
        final var blockAsItem = new ItemStack(state.getBlock().asItem());
        return level != null && !blockAsItem.isEmpty()
                ? ModRecipes.oreVacuum.getRecipeFor(level, new SingleRecipeInput(blockAsItem))
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
        return new ItemStack(emptyBlock);
    }

    @Override
    public RecipeSerializer<VacuumableOreRecipe> getSerializer() {
        return ModRecipes.oreVacuum.serializer();
    }

    @Override
    public RecipeType<VacuumableOreRecipe> getType() {
        return ModRecipes.oreVacuum.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.oreVacuum.bookCategory();
    }

    private static VacuumableOreRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        final var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        final var emptyBlock = BuiltInRegistries.BLOCK.getValue(buf.readIdentifier());
        return new VacuumableOreRecipe(ingredient, emptyBlock);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, VacuumableOreRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
        buf.writeIdentifier(BuiltInRegistries.BLOCK.getKey(recipe.emptyBlock));
    }

    public static RecipeSerializer<VacuumableOreRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
