package net.blay09.mods.replikaentropie.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiResearchCategory implements IRecipeCategory<ResearchRecipe> {

    public static final ResourceLocation UID = id("research");
    public static final RecipeType<ResearchRecipe> TYPE = new RecipeType<>(UID, ResearchRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/research.png");

    private final IDrawable icon;

    public JeiResearchCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.skyScraper));
    }

    @Override
    public RecipeType<ResearchRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(UID.toLanguageKey("jei"));
    }

    @Override
    public int getWidth() {
        return 104;
    }

    @Override
    public int getHeight() {
        return 76;
    }

    @Override
    public void draw(ResearchRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(TEXTURE, 0, 0, 0, 0, getWidth(), getHeight());
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ResearchRecipe recipe, IFocusGroup focuses) {
        if (recipe.dataCost() > 0) {
            builder.addInputSlot(6, 52)
                    .addItemStack(new ItemStack(ModItems.data, recipe.dataCost()));
        }

        if (recipe.scrapCost() > 0) {
            builder.addInputSlot(32, 52)
                    .addItemStack(new ItemStack(ModItems.scrap, recipe.scrapCost()));
        }

        if (recipe.biomassCost() > 0) {
            builder.addInputSlot(57, 52)
                    .addItemStack(new ItemStack(ModItems.biomass, recipe.biomassCost()));
        }

        if (recipe.fragmentsCost() > 0) {
            builder.addInputSlot(82, 52)
                    .addItemStack(new ItemStack(ModItems.fragments, recipe.fragmentsCost()));
        }

        if (recipe.type() == ResearchRecipe.Type.ASSEMBLER) {
            final var ticket = new ItemStack(ModItems.assemblyTicket);
            ticket.setHoverName(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.title", recipe.icon().getHoverName()));
            ticket.getOrCreateTag().putBoolean("ReplikaEntropieShowHint", true);
            builder.addOutputSlot(44, 14)
                    .addItemStack(ticket);

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 73, 14)
                    .addItemStack(recipe.icon());

            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                    .addItemStack(recipe.icon());
        } else if (recipe.type() == ResearchRecipe.Type.LORE) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 44, 14)
                    .addItemStack(recipe.icon());
        } else {
            builder.addOutputSlot(44, 14)
                    .addItemStack(recipe.icon());
        }
    }
}
