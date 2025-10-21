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

public class JeiBiomassHarvesterCategory implements IRecipeCategory<JeiBiomassHarvesterCategory.JeiBiomassHarvesterRecipe> {

    public record JeiBiomassHarvesterRecipe() {}

    public static final ResourceLocation UID = id("biomass_harvester");
    public static final RecipeType<JeiBiomassHarvesterRecipe> TYPE = new RecipeType<>(UID, JeiBiomassHarvesterRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/biomass_harvester.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiBiomassHarvesterCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.biomassHarvester));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 77, 76);
    }

    @Override
    public RecipeType<JeiBiomassHarvesterRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JeiBiomassHarvesterRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(30, 1).addItemLike(Items.DIAMOND_SWORD);
        builder.addInputSlot(1, 30).addItemLike(Items.DIAMOND_SWORD);
        builder.addInputSlot(60, 30).addItemLike(Items.DIAMOND_SWORD);
        builder.addInputSlot(30, 59).addItemLike(Items.DIAMOND_SWORD);
        builder.addOutputSlot(30, 30).addItemLike(ModItems.biomass);
    }
}
