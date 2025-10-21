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
import net.blay09.mods.replikaentropie.item.AssemblyTicketItem;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.AssemblerRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JeiAssemblerCategory implements IRecipeCategory<AssemblerRecipe> {

    public static final ResourceLocation UID = id("assembler");
    public static final RecipeType<AssemblerRecipe> TYPE = new RecipeType<>(UID, AssemblerRecipe.class);
    private static final ResourceLocation TEXTURE = id("textures/gui/jei/assembler.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public JeiAssemblerCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.assembler));
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 115, 59);
    }

    @Override
    public RecipeType<AssemblerRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblerRecipe recipe, IFocusGroup focuses) {
        final var ticket = new ItemStack(ModItems.assemblyTicket);
        ticket.setHoverName(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.title", recipe.result().getHoverName()));
        ticket.getOrCreateTag().putBoolean("ReplikaEntropieShowHint", true);
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 13)
                .addItemStack(ticket);

        int x = 26;
        int y = 42;
        for (int i = 0; i < recipe.ingredients().size(); i++) {
            final var countedIngredient = recipe.ingredients().get(i);
            final var items = new ArrayList<ItemStack>();
            for (final var itemStack : countedIngredient.ingredient().getItems()) {
                items.add(itemStack.copyWithCount(countedIngredient.count()));
            }
            builder.addInputSlot(x + i * 18, y)
                    .addItemStacks(items);
        }

        builder.addOutputSlot(62, 13)
                .addItemStack(recipe.result());
    }
}
