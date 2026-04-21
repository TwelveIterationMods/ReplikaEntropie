package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.DefragmentizerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class DefragmentizerScreen extends AbstractContainerScreen<DefragmentizerMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/defragmentizer.png");
    private final ProgressRenderer[] processingRenderers = new ProgressRenderer[4];
    private final ProgressRenderer[] fractionalRenderers = new ProgressRenderer[4];

    public DefragmentizerScreen(DefragmentizerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 207);

        inventoryLabelY = imageHeight - 94;

        for (int i = 0; i < 4; i++) {
            processingRenderers[i] = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256)
                    .pos(27 + i * 37, 46)
                    .size(16, 22)
                    .uv(176, 0);

            fractionalRenderers[i] = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256)
                    .pos(44 + i * 37, 75)
                    .size(3, 26)
                    .uv(176, 23);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        for (int i = 0; i < 4; i++) {
            processingRenderers[i].render(guiGraphics, leftPos, topPos, menu.getProcessingProgress(i));
            fractionalRenderers[i].render(guiGraphics, leftPos, topPos, menu.getFractionalFragments(i));
        }
    }

}
