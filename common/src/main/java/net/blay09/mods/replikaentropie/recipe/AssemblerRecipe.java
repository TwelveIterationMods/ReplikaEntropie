package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record AssemblerRecipe(List<CountedIngredient> ingredients,
                              ItemStackTemplate result) implements Recipe<RecipeInput>, PreviewableRecipe {
    private static final MapCodec<AssemblerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CountedIngredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(AssemblerRecipe::ingredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(AssemblerRecipe::result)
    ).apply(instance, AssemblerRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AssemblerRecipe> STREAM_CODEC = StreamCodec.composite(
            CountedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list(9)),
            AssemblerRecipe::ingredients,
            ItemStackTemplate.STREAM_CODEC,
            AssemblerRecipe::result,
            AssemblerRecipe::new
    );

    @Deprecated
    public boolean matches(Container container, Level level) {
        return matches(new ContainerInput(container), level);
    }

    @Deprecated
    public ItemStack assemble(Container container) {
        return assemble(new ContainerInput(container));
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        final var inputStacks = new ArrayList<ItemStack>();
        final var remainingCounts = new ArrayList<Integer>();
        for (int i = 0; i < input.size(); i++) {
            final var stack = input.getItem(i);
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
    public ItemStack assemble(RecipeInput input) {
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

    @Override
    public ItemStack previewResultItem() {
        return result.create();
    }

    @Override
    public RecipeSerializer<AssemblerRecipe> getSerializer() {
        return ModRecipes.assembler.serializer();
    }

    @Override
    public RecipeType<AssemblerRecipe> getType() {
        return ModRecipes.assembler.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredients.stream().map(CountedIngredient::ingredient).toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.assembler.bookCategory();
    }

    public static RecipeSerializer<AssemblerRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }

    private record ContainerInput(Container container) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return container.getItem(index);
        }

        @Override
        public int size() {
            return container.getContainerSize();
        }
    }
}
