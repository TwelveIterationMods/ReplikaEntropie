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
import net.blay09.mods.replikaentropie.block.entity.DefragmentizerBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiDefragmentizerCategory implements IRecipeCategory<RecyclerRecipe> {

    public static final ResourceLocation UID = id("defragmentizer");
    public static final RecipeType<RecyclerRecipe> TYPE = new RecipeType<>(UID, RecyclerRecipe.class);

    private static final ResourceLocation TEXTURE = id("textures/gui/jei/defragmentizer.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiDefragmentizerCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.defragmentizer));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 141, 80);
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
        builder.addInputSlot(5, 1)
                .addIngredients(recipe.ingredient());
        builder.addInputSlot(42, 1)
                .addIngredients(recipe.ingredient());
        builder.addInputSlot(79, 1)
                .addIngredients(recipe.ingredient());
        builder.addInputSlot(116, 1)
                .addIngredients(recipe.ingredient());

        final var fragments = (int) Math.ceil(recipe.fragments() * DefragmentizerBlockEntity.OUTPUT_MULTIPLIER);
        builder.addOutputSlot(5, 59)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragments, fragments));
        builder.addOutputSlot(42, 59)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragments, fragments));
        builder.addOutputSlot(79, 59)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragments, fragments));
        builder.addOutputSlot(116, 59)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragments, fragments));
    }

}
