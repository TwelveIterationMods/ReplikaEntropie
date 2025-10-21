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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiReplikaWorkbenchCategory implements IRecipeCategory<JeiReplikaWorkbenchCategory.JeiReplikaWorkbenchRecipe> {

    public record JeiReplikaWorkbenchRecipe(ItemStack frameItem, ItemStack assembledItem) { }

    public static final ResourceLocation UID = id("replika_workbench");
    public static final RecipeType<JeiReplikaWorkbenchRecipe> TYPE = new RecipeType<>(UID, JeiReplikaWorkbenchRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/replika_workbench.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiReplikaWorkbenchCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.replikaWorkbench));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 138, 76);
    }

    @Override
    public RecipeType<JeiReplikaWorkbenchRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JeiReplikaWorkbenchRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(30, 30)
                .addItemStack(recipe.frameItem());

        builder.addOutputSlot(117, 30)
                .addItemStack(recipe.assembledItem());
    }
}
