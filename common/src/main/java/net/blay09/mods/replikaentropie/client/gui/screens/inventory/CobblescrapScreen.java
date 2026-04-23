package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.CobblescrapMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class CobblescrapScreen extends AbstractContainerScreen<CobblescrapMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/cobblescrap.png");

    private final ProgressRenderer inputProgressLeftRenderer = SimpleProgressRenderer.horizontal(BACKGROUND, 256, 256).pos(34, 27).size(45, 6).uv(176, 22);
    private final ProgressRenderer inputProgressRightRenderer = SimpleProgressRenderer.reverseHorizontal(BACKGROUND, 256, 256).pos(97, 27).size(45, 6).uv(176, 22);
    private final ProgressRenderer outputProgressRenderer = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256).pos(80, 46).size(16, 22).uv(176, 0);
    private final ProgressRenderer scrapProgressRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(102, 75).size(3, 26).uv(208, 28);
    private final ProgressRenderer inputTankRenderer = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256).pos(17, 22).size(16, 48).uv(176, 28);
    private final ProgressRenderer outputTankRenderer = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256).pos(143, 22).size(16, 48).uv(192, 28);

    public CobblescrapScreen(CobblescrapMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 200);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        final var inputProgress = menu.getInputProcessingProgress();
        inputProgressLeftRenderer.render(graphics, leftPos, topPos, inputProgress);
        inputProgressRightRenderer.render(graphics, leftPos, topPos, inputProgress);
        outputProgressRenderer.render(graphics, leftPos, topPos, menu.getOutputProcessingProgress());
        scrapProgressRenderer.render(graphics, leftPos, topPos, menu.getFractionalScrap());
        inputTankRenderer.render(graphics, leftPos, topPos, 1f);
        outputTankRenderer.render(graphics, leftPos, topPos, 1f);
    }

}
