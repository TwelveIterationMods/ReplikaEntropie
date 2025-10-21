package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.LavascrapMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class LavascrapScreen extends AbstractContainerScreen<LavascrapMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/lavascrap.png");

    private final ProgressRenderer inputProgressLeftRenderer = SimpleProgressRenderer.horizontal(BACKGROUND, 256, 256).pos(34, 27).size(45, 6).uv(176, 22);
    private final ProgressRenderer inputProgressRightRenderer = SimpleProgressRenderer.reverseHorizontal(BACKGROUND, 256, 256).pos(97, 27).size(45, 6).uv(176, 22);
    private final ProgressRenderer outputProgressRenderer = SimpleProgressRenderer.vertical(BACKGROUND, 256, 256).pos(80, 46).size(16, 22).uv(176, 0);
    private final ProgressRenderer waterTankRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(17, 22).size(16, 52).uv(176, 28);
    private final ProgressRenderer lavaTankRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(143, 22).size(16, 52).uv(192, 28);
    private final ProgressRenderer fractionalScrapRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(102, 75).size(3, 26).uv(208, 28);

    public LavascrapScreen(LavascrapMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 200;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        final var inputProgress = menu.getInputProcessingProgress();
        inputProgressLeftRenderer.render(guiGraphics, leftPos, topPos, inputProgress);
        inputProgressRightRenderer.render(guiGraphics, leftPos, topPos, inputProgress);

        outputProgressRenderer.render(guiGraphics, leftPos, topPos, menu.getOutputProcessingProgress());

        waterTankRenderer.render(guiGraphics, leftPos, topPos, menu.getWaterTankProgress());
        lavaTankRenderer.render(guiGraphics, leftPos, topPos, menu.getLavaTankProgress());

        fractionalScrapRenderer.render(guiGraphics, leftPos, topPos, menu.getFractionalScrap());
    }

}
