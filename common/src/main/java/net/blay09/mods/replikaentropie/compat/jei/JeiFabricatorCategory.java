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
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiFabricatorCategory implements IRecipeCategory<FabricatorRecipe> {

    public static final ResourceLocation UID = id("fabricator");
    public static final RecipeType<FabricatorRecipe> TYPE = new RecipeType<>(UID, FabricatorRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/fabricator.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiFabricatorCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.fabricator));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 68, 80);
    }

    @Override
    public RecipeType<FabricatorRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, FabricatorRecipe recipe, IFocusGroup focuses) {
        if (recipe.scrap() > 0) {
            builder.addInputSlot(1, 1)
                    .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.scrap, recipe.scrap()));
        }
        if (recipe.biomass() > 0) {
            builder.addInputSlot(26, 1)
                    .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.biomass, recipe.biomass()));
        }
        if (recipe.fragments() > 0) {
            builder.addInputSlot(51, 1)
                    .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.fragments, recipe.fragments()));
        }

        builder.addOutputSlot(26, 59)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.result());
    }
}
