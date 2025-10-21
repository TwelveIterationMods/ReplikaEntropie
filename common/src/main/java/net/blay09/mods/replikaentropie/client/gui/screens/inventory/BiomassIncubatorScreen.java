package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.BiomassIncubatorMenu;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassIncubatorScreen extends AbstractContainerScreen<BiomassIncubatorMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/biomass_incubator.png");

    private final ProgressRenderer waterTank = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(26, 23).size(16, 48).uv(183, 30);
    private final SegmentedProgressRenderer wateringProgressRenderer;
    private final SegmentedProgressRenderer[] growthProgressRenderers;
    private final ProgressRenderer fractionalBiomass = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(147, 75).size(3, 26).uv(176, 0);

    public BiomassIncubatorScreen(BiomassIncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 203;
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        for (final var slot : menu.slots) {
            if (slot instanceof ReadonlySlot) {
                guiGraphics.blit(BACKGROUND, leftPos + slot.x, topPos + slot.y, 300, 183, 14, 16, 16, 256, 256);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        waterTank.render(guiGraphics, leftPos, topPos, menu.getWaterTankProgress());
        wateringProgressRenderer.render(guiGraphics, leftPos, topPos, menu.getWateringProgress());

        for (int i = 0; i < 3; i++) {
            growthProgressRenderers[i].render(guiGraphics, leftPos, topPos, menu.getGrowthProgress(i));
        }

        fractionalBiomass.render(guiGraphics, leftPos, topPos, menu.getFractionalBiomass());
    }

}
