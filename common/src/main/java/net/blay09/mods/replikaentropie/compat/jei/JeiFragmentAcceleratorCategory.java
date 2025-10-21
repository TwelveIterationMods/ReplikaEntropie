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
import net.blay09.mods.replikaentropie.block.entity.FragmentAcceleratorBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiFragmentAcceleratorCategory implements IRecipeCategory<RecyclerRecipe> {

    public static final ResourceLocation UID = id("fragment_accelerator");
    public static final RecipeType<RecyclerRecipe> TYPE = new RecipeType<>(UID, RecyclerRecipe.class);

    private static final ResourceLocation TEXTURE = id("textures/gui/jei/fragment_accelerator.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiFragmentAcceleratorCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.fragmentAccelerator));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 107, 76);
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
        builder.addInputSlot(30, 1)
                .addIngredients(recipe.ingredient());
        builder.addInputSlot(90, 30)
                .addIngredients(recipe.ingredient());
        builder.addInputSlot(30, 59)
                .addIngredients(recipe.ingredient());

        final var fragments = (int) Math.ceil(recipe.fragments() * FragmentAcceleratorBlockEntity.OUTPUT_MULTIPLIER);
        builder.addOutputSlot(30, 30)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragments, fragments));
        builder.addOutputSlot(61, 30)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragmentalWaste));
    }

}
