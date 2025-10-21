package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.RecyclerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class RecyclerScreen extends AbstractContainerScreen<RecyclerMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/recycler.png");

    private final SegmentedProgressRenderer topProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
            .addInvisibleSegment(28)
            .addReverseVerticalSegment(78, 29, 2, 32, 182, 32)
            .addHorizontalSegment(80, 23, 15, 16, 184, 26);
    private final SegmentedProgressRenderer middleProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
            .addHorizontalSegment(72, 61, 14, 4, 176, 63)
            .addHorizontalSegment(86, 55, 8, 16, 190, 57);
    private final SegmentedProgressRenderer bottomProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
            .addInvisibleSegment(28)
            .addVerticalSegment(78, 63, 2, 32, 182, 66)
            .addHorizontalSegment(80, 85, 15, 16, 184, 88);

    private final ProgressRenderer fractionalScrapRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(130, 19).size(3, 26).uv(176, 0);
    private final ProgressRenderer fractionalBiomassRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(130, 50).size(3, 26).uv(176, 0);
    private final ProgressRenderer fractionalFragmentsRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(130, 81).size(3, 26).uv(176, 0);

    public RecyclerScreen(RecyclerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 206;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        final var progress = menu.getProcessingProgress();
        if (progress > 0f) {
            topProgressRenderer.render(guiGraphics, leftPos, topPos, progress);
            middleProgressRenderer.render(guiGraphics, leftPos, topPos, progress);
            bottomProgressRenderer.render(guiGraphics, leftPos, topPos, progress);
        }

        fractionalScrapRenderer.render(guiGraphics, leftPos, topPos, menu.getFractionalScrap());
        fractionalBiomassRenderer.render(guiGraphics, leftPos, topPos, menu.getFractionalBiomass());
        fractionalFragmentsRenderer.render(guiGraphics, leftPos, topPos, menu.getFractionalFragments());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
