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

public class JeiLavascrapCategory implements IRecipeCategory<JeiLavascrapCategory.JeiLavascrapRecipe> {

    public record JeiLavascrapRecipe() {
    }

    public static final ResourceLocation UID = id("lavascrap");
    public static final RecipeType<JeiLavascrapRecipe> TYPE = new RecipeType<>(UID, JeiLavascrapRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/lavascrap.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiLavascrapCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.lavascrap));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 144, 80);
    }

    @Override
    public RecipeType<JeiLavascrapRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JeiLavascrapRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 58).addItemLike(Items.WATER_BUCKET);
        builder.addSlot(RecipeIngredientRole.CATALYST, 127, 58).addItemLike(Items.LAVA_BUCKET);

        builder.addInputSlot(64, 1).addItemLike(Items.OBSIDIAN);
        builder.addOutputSlot(64, 59).addItemLike(ModItems.scrap);
    }
}
