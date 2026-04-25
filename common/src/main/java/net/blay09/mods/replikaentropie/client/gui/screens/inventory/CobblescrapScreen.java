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

    private final ProgressRenderer progressLeftRenderer = SimpleProgressRenderer.horizontal(BACKGROUND, 256, 256).pos(34, 40).size(41, 6).uv(176, 22);
    private final ProgressRenderer progressRightRenderer = SimpleProgressRenderer.reverseHorizontal(BACKGROUND, 256, 256).pos(101, 40).size(41, 6).uv(180, 22);
    private final ProgressRenderer waterTankRenderer = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256).pos(17, 18).size(16, 48).uv(176, 28);
    private final ProgressRenderer lavaTankRenderer = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256).pos(143, 18).size(16, 48).uv(192, 28);

    public CobblescrapScreen(CobblescrapMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        final var inputProgress = menu.getProcessingProgress();
        progressLeftRenderer.render(graphics, leftPos, topPos, inputProgress);
        progressRightRenderer.render(graphics, leftPos, topPos, inputProgress);
        waterTankRenderer.render(graphics, leftPos, topPos, 1f);
        lavaTankRenderer.render(graphics, leftPos, topPos, 1f);
    }

}
