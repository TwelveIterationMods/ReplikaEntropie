package net.blay09.mods.replikaentropie.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class ExtendedGuiGraphics {

    /**
     * Same as GuiGraphicsExtractor.blitNineSliced except it doesn't center the fill
     */
    public static void blitNineSlicedTilingFill(GuiGraphicsExtractor guiGraphics, Identifier atlasLocation, int x, int y, int width, int height, int leftSliceWidth, int topSliceHeight, int rightSliceWidth, int bottomSliceHeight, int uWidth, int vHeight, int textureX, int textureY) {
        leftSliceWidth = Math.min(leftSliceWidth, width / 2);
        rightSliceWidth = Math.min(rightSliceWidth, width / 2);
        topSliceHeight = Math.min(topSliceHeight, height / 2);
        bottomSliceHeight = Math.min(bottomSliceHeight, height / 2);
        if (width == uWidth && height == vHeight) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, textureX, textureY, width, height, 256, 256);
        } else if (height == vHeight) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, textureX, textureY, leftSliceWidth, height, 256, 256);
            blitRepeatingTiling(guiGraphics, atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, height, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, vHeight);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + width - rightSliceWidth, y, textureX + uWidth - rightSliceWidth, textureY, rightSliceWidth, height, 256, 256);
        } else if (width == uWidth) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, textureX, textureY, width, topSliceHeight, 256, 256);
            blitRepeatingTiling(guiGraphics, atlasLocation, x, y + topSliceHeight, width, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, uWidth, vHeight - bottomSliceHeight - topSliceHeight);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y + height - bottomSliceHeight, textureX, textureY + vHeight - bottomSliceHeight, width, bottomSliceHeight, 256, 256);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y, textureX, textureY, leftSliceWidth, topSliceHeight, 256, 256);
            blitRepeatingTiling(guiGraphics, atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, topSliceHeight, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, topSliceHeight);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + width - rightSliceWidth, y, textureX + uWidth - rightSliceWidth, textureY, rightSliceWidth, topSliceHeight, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x, y + height - bottomSliceHeight, textureX, textureY + vHeight - bottomSliceHeight, leftSliceWidth, bottomSliceHeight, 256, 256);
            blitRepeatingTiling(guiGraphics, atlasLocation, x + leftSliceWidth, y + height - bottomSliceHeight, width - rightSliceWidth - leftSliceWidth, bottomSliceHeight, textureX + leftSliceWidth, textureY + vHeight - bottomSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, bottomSliceHeight);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + width - rightSliceWidth, y + height - bottomSliceHeight, textureX + uWidth - rightSliceWidth, textureY + vHeight - bottomSliceHeight, rightSliceWidth, bottomSliceHeight, 256, 256);
            blitRepeatingTiling(guiGraphics, atlasLocation, x, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
            blitRepeatingTiling(guiGraphics, atlasLocation, x + leftSliceWidth, y + topSliceHeight, width - rightSliceWidth - leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + leftSliceWidth, textureY + topSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
            blitRepeatingTiling(guiGraphics, atlasLocation, x + width - rightSliceWidth, y + topSliceHeight, rightSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + uWidth - rightSliceWidth, textureY + topSliceHeight, rightSliceWidth, vHeight - bottomSliceHeight - topSliceHeight);
        }
    }

    /**
     * Same as GuiGraphicsExtractor.blitRepeating except it doesn't center the fill
     */
    public static void blitRepeatingTiling(GuiGraphicsExtractor guiGraphics, Identifier atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset, int sourceWidth, int sourceHeight) {
        for (int dx = 0; dx < width; dx += sourceWidth) {
            int blitWidth = Math.min(sourceWidth, width - dx);
            for (int dy = 0; dy < height; dy += sourceHeight) {
                int blitHeight = Math.min(sourceHeight, height - dy);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, atlasLocation, x + dx, y + dy, uOffset, vOffset, blitWidth, blitHeight, 256, 256);
            }
        }
    }

    private static final double PERIOD_PER_SCROLLED_PIXEL = 0.5f;
    private static final double MIN_SCROLL_PERIOD = 3f;

    public static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        final var textWidth = font.width(text);
        final var regionWidth = maxX - minX;
        final var centerY = (minY + maxY - 9) / 2 + 1;
        if (textWidth > regionWidth) {
            final var overflowWidth = textWidth - regionWidth;
            final var seconds = (double) Util.getMillis() / (double) 1000f;
            final var scrollPeriod = Math.max(overflowWidth * 0.5f, MIN_SCROLL_PERIOD);
            final var t = Math.sin((Math.PI / 2f) * Math.cos((Math.PI * 2f) * seconds / scrollPeriod)) / 2f + PERIOD_PER_SCROLLED_PIXEL;
            final var offsetX = Mth.lerp(t, 0f, overflowWidth);
            guiGraphics.enableScissor(minX, minY, maxX, maxY);
            guiGraphics.text(font, text, minX - (int) offsetX, centerY, color, false);
            guiGraphics.disableScissor();
        } else {
            guiGraphics.text(font, text, minX, centerY, color, false);
        }

    }
}
