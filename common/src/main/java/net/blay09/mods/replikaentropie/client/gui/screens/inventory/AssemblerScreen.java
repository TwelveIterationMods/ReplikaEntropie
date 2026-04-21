package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.menu.AssemblerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class AssemblerScreen extends AbstractContainerScreen<AssemblerMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/assembler.png");

    private final SegmentedProgressRenderer progressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
            .addReverseVerticalSegment(45, 43, 2, 39, 176, 0)
            .addReverseVerticalSegment(129, 43, 2, 39, 176, 0)
            .addReverseVerticalSegment(63, 43, 2, 39, 176, 0)
            .addReverseVerticalSegment(111, 43, 2, 39, 176, 0)
            .addParallelSegments(
                    new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                            .addVerticalSegment(87, 43, 2, 5, 194, 20)
                            .addReverseHorizontalSegment(71, 45, 16, 3, 178, 22)
                            .addVerticalSegment(71, 48, 2, 17, 178, 25)
                            .addHorizontalSegment(73, 62, 2, 3, 180, 39)
                            .addInvisibleSegment(8),
                    new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                            .addReverseVerticalSegment(87, 77, 2, 5, 178, 15)
                            .addHorizontalSegment(89, 77, 16, 3, 180, 15)
                            .addReverseVerticalSegment(103, 62, 2, 15, 194, 0)
                            .addReverseHorizontalSegment(101, 62, 2, 3, 192, 0)
                            .addInvisibleSegment(8)
            );

    public AssemblerScreen(AssemblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 207);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        progressRenderer.render(graphics, leftPos, topPos, menu.getAssemblyProgress());
    }
}
