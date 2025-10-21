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
import net.blay09.mods.replikaentropie.recipe.BiomassIncubatorRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiBiomassIncubatorCategory implements IRecipeCategory<BiomassIncubatorRecipe> {

    public static final ResourceLocation UID = id("biomass_incubator");
    public static final RecipeType<BiomassIncubatorRecipe> TYPE = new RecipeType<>(UID, BiomassIncubatorRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/biomass_incubator.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiBiomassIncubatorCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.biomassIncubator));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 125, 79);
    }

    @Override
    public RecipeType<BiomassIncubatorRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, BiomassIncubatorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 58)
                .addItemLike(Items.WATER_BUCKET);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 26, 19)
                .addItemLike(Items.FARMLAND);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 44, 19)
                .addItemLike(Items.FARMLAND);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 62, 19)
                .addItemLike(Items.FARMLAND);

        builder.addInputSlot(26, 1)
                .addIngredients(recipe.ingredient());

        builder.addInputSlot(44, 1)
                .addIngredients(recipe.ingredient());

        builder.addInputSlot(62, 1)
                .addIngredients(recipe.ingredient());

        builder.addOutputSlot(100, 58)
                .addItemStack(new ItemStack(ModItems.biomass, (int) Math.ceil(recipe.biomass())));
    }
}
