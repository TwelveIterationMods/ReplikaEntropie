package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.menu.BluePrinterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BluePrinterScreen extends AbstractContainerScreen<BluePrinterMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/blue_printer.png");

    public BluePrinterScreen(BluePrinterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, DEFAULT_IMAGE_WIDTH, 174);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        final var progress = menu.getProcessingProgress();
        if (progress > 0f) {
            final var width = Math.max(1, Math.round(24 * progress));
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + 104, topPos + 49, 176, 0, width, 16, 256, 256);
        }
    }
}
