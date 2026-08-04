package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.EnergyBar;
import net.blay09.mods.replikaentropie.client.gui.components.MakeshiftPowerButton;
import net.blay09.mods.replikaentropie.menu.FabricatorMenu;
import net.blay09.mods.replikaentropie.menu.slot.FabricatorBufferSlot;
import net.blay09.mods.replikaentropie.menu.slot.FabricatorRecipeSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Optional;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/fabricator.png");
    private static final Identifier LEFT_WING = id("left_wing");
    private final SegmentedProgressRenderer outputProgressRenderer;
    private final EnergyBar energyBar = new EnergyBar(-23, 11);

    public FabricatorScreen(FabricatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 222);

        inventoryLabelY = imageHeight - 94;

        outputProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(129, 28, 26, 4, 176, 16)
                .addVerticalSegment(146, 32, 15, 10, 193, 20);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new MakeshiftPowerButton(leftPos - 25, topPos + 102, menu.containerId, menu::isMakeshiftPsuOverheated));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        for (final var slot : menu.slots) {
            if (slot instanceof FabricatorRecipeSlot fabricatorRecipeSlot) {
                if (menu.isInfinitelyQueued(fabricatorRecipeSlot)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y, 176, 30, 16, 16, 256, 256);
                } else {
                    final var count = menu.getQueuedCount(fabricatorRecipeSlot);
                    if (count > 0) {
                        final var stringCount = String.valueOf(count);
                        final var stringWidth = font.width(stringCount);
                        graphics.text(font, stringCount, leftPos + slot.x + 16 - stringWidth, topPos + slot.y + 8, 0xFFFFFFFF, true);
                    }
                }
            } else if (slot instanceof FabricatorBufferSlot) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y + 1, 198, 0, 16, 16, 256, 256);
            }
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + 145, topPos + 46, 145, 46, 18, 4, 256, 256);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 27, topPos + 7, 24, 90);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 29, topPos + 98, 28, 28);

        if (menu.isMissingScrap()) {
            final var x = leftPos + 37;
            final var y = topPos + 22;
            graphics.fill(x, y, x + 16, y + 16, 0xFFA17171);
        }

        if (menu.isMissingBiomass()) {
            final var x = leftPos + 62;
            final var y = topPos + 22;
            graphics.fill(x, y, x + 16, y + 16, 0xFFA17171);
        }

        if (menu.isMissingFragments()) {
            final var x = leftPos + 87;
            final var y = topPos + 22;
            graphics.fill(x, y, x + 16, y + 16, 0xFFA17171);
        }

        final var outputProgress = menu.getOutputProcessingProgress();
        if (outputProgress > 0f) {
            outputProgressRenderer.render(graphics, leftPos, topPos, outputProgress);
        }

        energyBar.render(graphics, leftPos, topPos, menu.getPowerProgress());
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (energyBar.extractTooltip(graphics, font, leftPos, topPos, mouseX, mouseY, menu.getCurrentPower(), menu.getMaxPower())) {
            return;
        }

        if (hoveredSlot instanceof FabricatorRecipeSlot recipeSlot) {
            final var display = menu.getDisplay(recipeSlot);
            if (display != null) {
                final var itemStack = recipeSlot.getItem();
                final var tooltip = new ArrayList<>(getTooltipFromContainerItem(itemStack));

                if (display.scrap() > 0) {
                    tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.scrap", display.scrap()).withStyle(ChatFormatting.DARK_AQUA));
                }

                if (display.biomass() > 0) {
                    tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.biomass", display.biomass()).withStyle(ChatFormatting.DARK_GREEN));
                }

                if (display.fragments() > 0) {
                    tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.fragments", display.fragments()).withStyle(ChatFormatting.DARK_PURPLE));
                }

                if (display.scrap() == 0 && display.biomass() == 0 && display.fragments() == 0) {
                    tooltip.add(Component.translatable("gui.replikaentropie.fabricator.tooltip.free").withStyle(ChatFormatting.GREEN));
                }

                if (menu.getCarried().isEmpty()) {
                    graphics.setTooltipForNextFrame(font, tooltip, Optional.empty(), mouseX, mouseY);
                }
            }
        } else {
            super.extractTooltip(graphics, mouseX, mouseY);
        }
    }

}
