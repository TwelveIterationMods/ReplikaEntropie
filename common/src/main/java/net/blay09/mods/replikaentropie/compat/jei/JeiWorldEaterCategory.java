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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiWorldEaterCategory implements IRecipeCategory<JeiWorldEaterCategory.JeiWorldEaterRecipe> {

    public record JeiWorldEaterRecipe() {
    }

    public static final RandomSource random = RandomSource.create();

    public static final ResourceLocation UID = id("world_eater");
    public static final RecipeType<JeiWorldEaterRecipe> TYPE = new RecipeType<>(UID, JeiWorldEaterRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/world_eater.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiWorldEaterCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.worldEater));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 143, 58);
    }

    @Override
    public RecipeType<JeiWorldEaterRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JeiWorldEaterRecipe recipe, IFocusGroup focuses) {
        final var candidates = new ItemLike[] {
                Items.STONE,
                Items.DIORITE,
                Items.GRANITE,
                Items.GRAVEL,
                Items.DEEPSLATE
        };

        int x = 6;
        int y = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x + i * 18, y + j * 18)
                        .addItemLike(candidates[random.nextInt(candidates.length)]);
            }
        }

        builder.addOutputSlot(118, 37)
                .addItemStack(new ItemStack(ModItems.scrap));
    }
}
