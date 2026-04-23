package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
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
    private final ProgressRenderer fractionalBiomass = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(155, 48).size(3, 26).uv(176, 0);

    public BiomassIncubatorScreen(BiomassIncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 203);

        inventoryLabelY = imageHeight - 94;
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
        fractionalBiomass.render(graphics, leftPos, topPos, menu.getFractionalBiomass());
    }

}
