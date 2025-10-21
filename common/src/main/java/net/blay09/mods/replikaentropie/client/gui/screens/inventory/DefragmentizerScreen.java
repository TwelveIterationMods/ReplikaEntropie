package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.DefragmentizerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class DefragmentizerScreen extends AbstractContainerScreen<DefragmentizerMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/defragmentizer.png");
    private final ProgressRenderer[] processingRenderers = new ProgressRenderer[4];
    private final ProgressRenderer[] fractionalRenderers = new ProgressRenderer[4];

    public DefragmentizerScreen(DefragmentizerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 207;
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        for (int i = 0; i < 4; i++) {
            processingRenderers[i].render(guiGraphics, leftPos, topPos, menu.getProcessingProgress(i));
            fractionalRenderers[i].render(guiGraphics, leftPos, topPos, menu.getFractionalFragments(i));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
