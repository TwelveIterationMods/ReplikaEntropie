package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.LavascrapMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class LavascrapScreen extends AbstractContainerScreen<LavascrapMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/lavascrap.png");

    private final ProgressRenderer progressLeftRenderer = SimpleProgressRenderer.horizontal(BACKGROUND, 256, 256).pos(34, 27).size(45, 6).uv(176, 22);
    private final ProgressRenderer progressRightRenderer = SimpleProgressRenderer.reverseHorizontal(BACKGROUND, 256, 256).pos(97, 27).size(45, 6).uv(176, 22);
    private final ProgressRenderer waterTankRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(17, 22).size(16, 52).uv(176, 28);
    private final ProgressRenderer lavaTankRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(143, 22).size(16, 52).uv(192, 28);

    public LavascrapScreen(LavascrapMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 198);

        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        final var inputProgress = menu.getProcessingProgress();
        progressLeftRenderer.render(graphics, leftPos, topPos, inputProgress);
        progressRightRenderer.render(graphics, leftPos, topPos, inputProgress);
        waterTankRenderer.render(graphics, leftPos, topPos, menu.getWaterTankProgress());
        lavaTankRenderer.render(graphics, leftPos, topPos, menu.getLavaTankProgress());
    }

}
