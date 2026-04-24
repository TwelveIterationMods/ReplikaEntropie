package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.Optional;

public record MetalDetectorRecipe(BlockState state, float chance, ResourceKey<LootTable> lootTable) implements Recipe<RecipeInput> {

    private static final MapCodec<MetalDetectorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(MetalDetectorRecipe::state),
            Codec.FLOAT.fieldOf("chance").forGetter(MetalDetectorRecipe::chance),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(MetalDetectorRecipe::lootTable)
    ).apply(instance, MetalDetectorRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, MetalDetectorRecipe> STREAM_CODEC = StreamCodec.of(
            MetalDetectorRecipe::toNetwork,
            MetalDetectorRecipe::fromNetwork
    );

    public static Optional<MetalDetectorRecipe> getRecipe(Level level, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        final var candidates = new ArrayList<MetalDetectorRecipe>();
        for (final var recipeHolder : serverLevel.recipeAccess().getRecipes()) {
            if (recipeHolder.value().getType() == ModRecipes.metalDetector.type() && recipeHolder.value() instanceof MetalDetectorRecipe metalDetectorRecipe && metalDetectorRecipe.state.equals(state)) {
                candidates.add(metalDetectorRecipe);
            }
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(candidates.get(level.getRandom().nextInt(candidates.size())));
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
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
    public RecipeSerializer<MetalDetectorRecipe> getSerializer() {
        return ModRecipes.metalDetector.serializer();
    }

    @Override
    public RecipeType<MetalDetectorRecipe> getType() {
        return ModRecipes.metalDetector.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.metalDetector.bookCategory();
    }

    private static MetalDetectorRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        final var state = buf.readLenientJsonWithCodec(BlockState.CODEC);
        final var chance = buf.readFloat();
        final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
        return new MetalDetectorRecipe(state, chance, lootTable);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, MetalDetectorRecipe recipe) {
        buf.writeJsonWithCodec(BlockState.CODEC, recipe.state);
        buf.writeFloat(recipe.chance);
        buf.writeResourceKey(recipe.lootTable);
    }

    public static RecipeSerializer<MetalDetectorRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
