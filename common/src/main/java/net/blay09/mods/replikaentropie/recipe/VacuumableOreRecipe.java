package net.blay09.mods.replikaentropie.recipe;

import com.google.gson.JsonObject;
import net.blay09.mods.replikaentropie.container.SingleItemContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

// POSTJAM Should we make this use BlockState instead of Ingredient?
public record VacuumableOreRecipe(ResourceLocation id, Ingredient ingredient, Block emptyBlock) implements Recipe<Container> {

    public static Optional<VacuumableOreRecipe> getRecipe(Level level, BlockState state) {
        final var blockAsItem = new ItemStack(state.getBlock().asItem());
        return level != null && !blockAsItem.isEmpty()
                ? level.getRecipeManager().getRecipeFor(ModRecipes.vacuumableOreType, new SingleItemContainer(blockAsItem), level)
                : Optional.empty();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.vacuumableOreSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.vacuumableOreType;
    }

    public static class Serializer implements RecipeSerializer<VacuumableOreRecipe> {
        @Override
        public VacuumableOreRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var ingredient = Ingredient.fromJson(json.get("ingredient"));
            final var emptyBlockId = new ResourceLocation(json.get("empty_block").getAsString());
            final var emptyBlock = BuiltInRegistries.BLOCK.get(emptyBlockId);
            return new VacuumableOreRecipe(id, ingredient, emptyBlock);
            
        }

        @Override
        public VacuumableOreRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredient = Ingredient.fromNetwork(buf);
            final var emptyBlock = buf.readById(BuiltInRegistries.BLOCK);
            return new VacuumableOreRecipe(id, ingredient, emptyBlock);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, VacuumableOreRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeId(BuiltInRegistries.BLOCK, recipe.emptyBlock);
        }
    }
}
