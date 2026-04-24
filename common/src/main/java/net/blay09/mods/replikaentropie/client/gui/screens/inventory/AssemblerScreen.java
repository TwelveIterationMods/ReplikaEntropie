package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.EnergyBar;
import net.blay09.mods.replikaentropie.client.gui.components.MakeshiftPowerButton;
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
    private static final Identifier LEFT_WING = id("left_wing");

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
    private final EnergyBar energyBar = new EnergyBar(-23, 11);

    public AssemblerScreen(AssemblerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, DEFAULT_IMAGE_WIDTH, 222);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new MakeshiftPowerButton(leftPos - 25, topPos + 102, menu.containerId));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 27, topPos + 7, 24, 90);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 29, topPos + 98, 28, 28);

        progressRenderer.render(graphics, leftPos, topPos, menu.getAssemblyProgress());
        energyBar.render(graphics, leftPos, topPos, menu.getPowerProgress());
    }
}
