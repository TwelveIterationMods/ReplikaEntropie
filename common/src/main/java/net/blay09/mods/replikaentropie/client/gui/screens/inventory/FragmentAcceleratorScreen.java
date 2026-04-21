package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.FragmentAcceleratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FragmentAcceleratorScreen extends AbstractContainerScreen<FragmentAcceleratorMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/fragment_accelerator.png");
    private final SegmentedProgressRenderer progressRenderer;
    private final ProgressRenderer fractionalFragmentsRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(83, 50).size(3, 26).uv(176, 0);

    public FragmentAcceleratorScreen(FragmentAcceleratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 206);

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
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        progressRenderer.render(graphics, leftPos, topPos, menu.getProcessingProgress());
        fractionalFragmentsRenderer.render(graphics, leftPos, topPos, menu.getFractionalFragments());
    }

}
