package net.blay09.mods.replikaentropie.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiCobblescrapCategory implements IRecipeCategory<JeiCobblescrapCategory.JeiCobblescrapRecipe> {

    public record JeiCobblescrapRecipe() {}

    public static final ResourceLocation UID = id("cobblescrap");
    public static final RecipeType<JeiCobblescrapRecipe> TYPE = new RecipeType<>(UID, JeiCobblescrapRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/cobblescrap.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiCobblescrapCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.cobblescrap));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 144, 80);
    }

    @Override
    public RecipeType<JeiCobblescrapRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JeiCobblescrapRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(64, 1).addItemLike(Items.COBBLESTONE);
        builder.addOutputSlot(64, 59).addItemLike(ModItems.scrap);
    }
}
