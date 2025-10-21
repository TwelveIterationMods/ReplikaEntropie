package net.blay09.mods.replikaentropie.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiRecyclerCategory implements IRecipeCategory<RecyclerRecipe> {

    public static final ResourceLocation UID = id("recycler");
    public static final RecipeType<RecyclerRecipe> TYPE = new RecipeType<>(UID, RecyclerRecipe.class);

    private static final ResourceLocation TEXTURE = id("textures/gui/jei/recycler.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiRecyclerCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.recycler));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 86, 88);
    }

    @Override
    public RecipeType<RecyclerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(UID.toLanguageKey("jei"));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecyclerRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 36)
                .addIngredients(recipe.ingredient());

        builder.addOutputSlot(61, 5)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.scrap() > 0f
                        ? new ItemStack(ModItems.scrap, (int) Math.ceil(recipe.scrap()))
                        : ItemStack.EMPTY);

        builder.addOutputSlot(61, 36)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.biomass() > 0f
                        ? new ItemStack(ModItems.biomass, (int) Math.ceil(recipe.biomass()))
                        : ItemStack.EMPTY);

        builder.addOutputSlot(61, 67)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.fragments() > 0f
                        ? new ItemStack(ModItems.fragments, (int) Math.ceil(recipe.fragments()))
                        : ItemStack.EMPTY);
    }

}
