package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.BiomassHarvesterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassHarvesterScreen extends AbstractContainerScreen<BiomassHarvesterMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/biomass_harvester.png");

    private final ProgressRenderer fractionalBiomass = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(100, 50).size(3, 26).uv(176, 0);

    public BiomassHarvesterScreen(BiomassHarvesterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 206);

        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        fractionalBiomass.render(graphics, leftPos, topPos, menu.getFractionalBiomass());
    }

}
