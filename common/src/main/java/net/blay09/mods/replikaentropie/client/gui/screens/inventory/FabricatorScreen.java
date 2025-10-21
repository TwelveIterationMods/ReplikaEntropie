package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.container.RecipeContainer;
import net.blay09.mods.replikaentropie.menu.FabricatorMenu;
import net.blay09.mods.replikaentropie.menu.slot.FabricatorBufferSlot;
import net.blay09.mods.replikaentropie.menu.slot.FabricatorRecipeSlot;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/fabricator.png");
    private final SegmentedProgressRenderer outputProgressRenderer;

    public FabricatorScreen(FabricatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 222;
        inventoryLabelY = imageHeight - 94;

        outputProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(129, 28, 26, 4, 176, 16)
                .addVerticalSegment(146, 32, 15, 10, 193, 20);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (final var slot : menu.slots) {
            if (slot instanceof FabricatorRecipeSlot fabricatorRecipeSlot) {
                if (menu.isInfinitelyQueued(fabricatorRecipeSlot)) {
                    guiGraphics.blit(BACKGROUND, leftPos + slot.x, topPos + slot.y, 300, 176, 30, 16, 16, 256, 256);
                } else {
                    final var count = menu.getQueuedCount(fabricatorRecipeSlot);
                    if (count > 0) {
                        final var stringCount = String.valueOf(count);
                        final var stringWidth = font.width(stringCount);
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, 0, 300);
                        guiGraphics.drawString(font, stringCount, leftPos + slot.x + 16 - stringWidth, topPos + slot.y + 8, 0xFFFFFFFF, true);
                        guiGraphics.pose().pushPose();
                    }
                }
            } else if (slot instanceof FabricatorBufferSlot) {
                guiGraphics.blit(BACKGROUND, leftPos + slot.x, topPos + slot.y + 1, 300, 198, 0, 16, 16, 256, 256);
            }
        }

        guiGraphics.blit(BACKGROUND, leftPos + 145, topPos + 46, 300, 145, 46, 18, 4, 256, 256);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.isMissingScrap()) {
            final var x = leftPos + 37;
            final var y = topPos + 22;
            guiGraphics.fill(x, y, x + 16, y + 16, 0xFFA17171);
        }

        if (menu.isMissingBiomass()) {
            final var x = leftPos + 62;
            final var y = topPos + 22;
            guiGraphics.fill(x, y, x + 16, y + 16, 0xFFA17171);
        }

        if (menu.isMissingFragments()) {
            final var x = leftPos + 87;
            final var y = topPos + 22;
            guiGraphics.fill(x, y, x + 16, y + 16, 0xFFA17171);
        }

        final var outputProgress = menu.getOutputProcessingProgress();
        if (outputProgress > 0f) {
            outputProgressRenderer.render(guiGraphics, leftPos, topPos, outputProgress);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (hoveredSlot instanceof FabricatorRecipeSlot recipeSlot) {
            final var containerSlot = recipeSlot.getContainerSlot();
            if (recipeSlot.container instanceof RecipeContainer<?> recipeContainer) {
                @SuppressWarnings("unchecked") final var recipe = ((RecipeContainer<FabricatorRecipe>) recipeContainer).getRecipe(containerSlot);
                if (recipe != null) {
                    final var itemStack = recipeSlot.getItem();
                    final var tooltip = new ArrayList<>(getTooltipFromContainerItem(itemStack));

                    if (recipe.scrap() > 0) {
                        tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.scrap", recipe.scrap()).withStyle(ChatFormatting.DARK_AQUA));
                    }

                    if (recipe.biomass() > 0) {
                        tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.biomass", recipe.biomass()).withStyle(ChatFormatting.DARK_GREEN));
                    }

                    if (recipe.fragments() > 0) {
                        tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.fragments", recipe.fragments()).withStyle(ChatFormatting.DARK_PURPLE));
                    }

                    if(recipe.scrap() == 0 && recipe.biomass() == 0 &&recipe.fragments() == 0) {
                        tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.free").withStyle(ChatFormatting.GREEN));
                    }

                    guiGraphics.renderTooltip(font, tooltip, itemStack.getTooltipImage(), mouseX, mouseY);
                }
            }
        } else {
            super.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

}
