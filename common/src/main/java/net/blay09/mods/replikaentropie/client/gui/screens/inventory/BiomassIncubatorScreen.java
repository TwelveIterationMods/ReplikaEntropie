package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.BiomassIncubatorMenu;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassIncubatorScreen extends AbstractContainerScreen<BiomassIncubatorMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/biomass_incubator.png");

    private final ProgressRenderer waterTank = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(26, 23).size(16, 48).uv(183, 30);
    private final SegmentedProgressRenderer wateringProgressRenderer;
    private final SegmentedProgressRenderer[] growthProgressRenderers;
    private final ProgressRenderer fractionalBiomass = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(147, 75).size(3, 26).uv(176, 0);

    public BiomassIncubatorScreen(BiomassIncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 203);

        inventoryLabelY = imageHeight - 94;

        wateringProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(43, 61, 15, 6, 183, 8)
                .addReverseVerticalSegment(56, 58, 2, 3, 196, 4)
                .addHorizontalSegment(58, 61, 20, 5, 198, 8)
                .addReverseVerticalSegment(76, 58, 2, 4, 216, 4)
                .addHorizontalSegment(78, 61, 20, 5, 218, 8)
                .addReverseVerticalSegment(96, 57, 2, 4, 236, 4);

        growthProgressRenderers = new SegmentedProgressRenderer[3];
        growthProgressRenderers[0] = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(104, 23, 40, 4, 179, 0)
                .addVerticalSegment(140, 26, 4, 50, 179, 5);

        growthProgressRenderers[1] = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(104, 28, 31, 4, 179, 0)
                .addVerticalSegment(131, 31, 4, 44, 179, 4);

        growthProgressRenderers[2] = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(104, 33, 22, 4, 179, 0)
                .addVerticalSegment(122, 36, 4, 39, 179, 4);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        for (final var slot : menu.slots) {
            if (slot instanceof ReadonlySlot) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y, 183, 14, 16, 16, 256, 256);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        waterTank.render(graphics, leftPos, topPos, menu.getWaterTankProgress());
        wateringProgressRenderer.render(graphics, leftPos, topPos, menu.getWateringProgress());

        for (int i = 0; i < 3; i++) {
            growthProgressRenderers[i].render(graphics, leftPos, topPos, menu.getGrowthProgress(i));
        }

        fractionalBiomass.render(graphics, leftPos, topPos, menu.getFractionalBiomass());
    }

}
