package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.EnergyBar;
import net.blay09.mods.replikaentropie.client.gui.components.MakeshiftPowerButton;
import net.blay09.mods.replikaentropie.menu.WorldEaterMenu;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class WorldEaterScreen extends AbstractContainerScreen<WorldEaterMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/world_eater.png");
    private static final Identifier LEFT_WING = id("left_wing");
    private final SegmentedProgressRenderer scanningProgressRenderer;
    private final EnergyBar energyBar = new EnergyBar(-23, 5);

    public WorldEaterScreen(WorldEaterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 180);

        inventoryLabelY = imageHeight - 94;

        scanningProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addInvisibleSegment(20)
                .addHorizontalSegment(14, 66, 3, 5, 194, 0)
                .addInvisibleSegment(90)
                .addHorizontalSegment(107, 66, 2, 5, 197, 18)
                .addReverseVerticalSegment(109, 53, 3, 18, 199, 5)
                .addReverseHorizontalSegment(107, 49, 5, 5, 197, 0)
                .addInvisibleSegment(90)
                .addReverseHorizontalSegment(15, 49, 2, 5, 195, 36)
                .addReverseVerticalSegment(12, 36, 3, 18, 192, 23)
                .addHorizontalSegment(12, 31, 5, 5, 192, 18)
                .addInvisibleSegment(90)
                .addHorizontalSegment(107, 24, 11, 16, 197, 30);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new MakeshiftPowerButton(leftPos - 25, topPos + 96, menu.containerId, menu::isMakeshiftPsuOverheated));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        for (final var slot : menu.slots) {
            if (slot instanceof ReadonlySlot) {
                if (menu.isDestroying() && menu.getCurrentDestroySlot() == slot.getContainerSlot()) {
                    final var destroyProgress = menu.getDestroyingProgress();
                    final var frameIndex = Mth.clamp((int) (destroyProgress * 8), 0, 8);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y, 176, 16 + frameIndex * 16, 16, 16, 256, 256);
                }

                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y, 176, 0, 16, 16, 256, 256);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 27, topPos + 1, 24, 90);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 29, topPos + 92, 28, 28);

        if (menu.isScanning()) {
            scanningProgressRenderer.render(graphics, leftPos, topPos, menu.getScanningProgress());
        } else if (menu.isDestroying()) {
            scanningProgressRenderer.render(graphics, leftPos, topPos, 1f);
        }

        energyBar.render(graphics, leftPos, topPos, menu.getPowerProgress());
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (energyBar.extractTooltip(graphics, font, leftPos, topPos, mouseX, mouseY, menu.getCurrentPower(), menu.getMaxPower())) {
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

}
