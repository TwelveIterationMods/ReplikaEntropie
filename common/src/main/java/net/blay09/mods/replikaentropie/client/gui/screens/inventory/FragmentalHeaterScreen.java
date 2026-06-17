package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.FragmentalHeaterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FragmentalHeaterScreen extends AbstractContainerScreen<FragmentalHeaterMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/fragmental_heater.png");
    private final ProgressRenderer[] processingRenderers = new ProgressRenderer[6];

    public FragmentalHeaterScreen(FragmentalHeaterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 207);

        inventoryLabelY = imageHeight - 94;

        processingRenderers[0] = inputProgressRenderer(46, 39);
        processingRenderers[1] = inputProgressRenderer(107, 39);
        processingRenderers[2] = inputProgressRenderer(21, 64);
        processingRenderers[3] = inputProgressRenderer(132, 64);
        processingRenderers[4] = inputProgressRenderer(46, 89);
        processingRenderers[5] = inputProgressRenderer(107, 89);
    }

    private static SimpleProgressRenderer inputProgressRenderer(int x, int y) {
        return SimpleProgressRenderer.horizontal(BACKGROUND, 256, 256)
                .pos(x, y)
                .size(18, 5)
                .uv(176, 0);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        for (int i = 0; i < processingRenderers.length; i++) {
            processingRenderers[i].render(guiGraphics, leftPos, topPos, menu.getProcessingProgress(i));
        }

        final var temperatureProgress = menu.getTemperatureProgress();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + 22 + (int) (123 * temperatureProgress), topPos + 98, 176, 5, 4, 11, 256, 256);
        if (!menu.canExtractOutput()) {
            final var frameIndex = (int) (Util.getMillis() / 200) % 2;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + 72, topPos + 43, 176 + frameIndex * 26, 16, 26, 26, 256, 256);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (!menu.canExtractOutput() && isHovering(72, 43, 26, 26, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.replikaentropie.fragmental_heater.cooling_required").withStyle(ChatFormatting.RED), mouseX, mouseY);
            return;
        }

        if (isHovering(22, 98, 127, 11, mouseX, mouseY) && menu.getTemperature() > 0) {
            graphics.setTooltipForNextFrame(font, menu.getProgressTooltip(), mouseX, mouseY);
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

}
