package net.blay09.mods.replikaentropie.container;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public class RecipeContainer<TRecipe extends Recipe<?>> extends SimpleContainer {

    private final Int2ObjectMap<TRecipe> recipes = new Int2ObjectOpenHashMap<>();

    public RecipeContainer(int size) {
        super(size);
    }

    public void setRecipe(RegistryAccess registryAccess, int slot, @Nullable TRecipe recipe) {
        if (recipe != null) {
            recipes.put(slot, recipe);
            setItem(slot, recipe.getResultItem(registryAccess));
        } else {
            recipes.remove(slot);
        }
    }

    @Nullable
    public TRecipe getRecipe(int slot) {
        return recipes.get(slot);
    }
}
