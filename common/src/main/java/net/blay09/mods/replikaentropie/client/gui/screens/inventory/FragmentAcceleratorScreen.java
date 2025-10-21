package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.FragmentAcceleratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FragmentAcceleratorScreen extends AbstractContainerScreen<FragmentAcceleratorMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/fragment_accelerator.png");
    private final SegmentedProgressRenderer progressRenderer;
    private final ProgressRenderer fractionalFragmentsRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(83, 50).size(3, 26).uv(176, 0);

    public FragmentAcceleratorScreen(FragmentAcceleratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 206;
        inventoryLabelY = imageHeight - 94;

        progressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addHorizontalSegment(78, 32, 13, 3, 181, 0)
                .addHorizontalSegment(109, 32, 19, 3, 181, 0)
                .addVerticalSegment(128, 34, 2, 20, 179, 0)
                .addVerticalSegment(128, 72, 2, 19, 179, 1)
                .addReverseHorizontalSegment(109, 90, 19, 3, 181, 0)
                .addReverseHorizontalSegment(78, 90, 13, 3, 181, 0)
                .addReverseHorizontalSegment(41, 90, 19, 3, 181, 0)
                .addReverseVerticalSegment(39, 72, 2, 19, 179, 1)
                .addReverseVerticalSegment(39, 34, 2, 20, 179, 0)
                .addHorizontalSegment(41, 32, 19, 3, 181, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        progressRenderer.render(guiGraphics, leftPos, topPos, menu.getProcessingProgress());
        fractionalFragmentsRenderer.render(guiGraphics, leftPos, topPos, menu.getFractionalFragments());
    }

}
