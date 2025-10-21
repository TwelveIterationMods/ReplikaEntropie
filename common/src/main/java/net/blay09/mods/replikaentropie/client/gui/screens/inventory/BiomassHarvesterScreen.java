package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.BiomassHarvesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassHarvesterScreen extends AbstractContainerScreen<BiomassHarvesterMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/biomass_harvester.png");

    private final ProgressRenderer fractionalBiomass = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(100, 50).size(3, 26).uv(176, 0);

    public BiomassHarvesterScreen(BiomassHarvesterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 206;
        inventoryLabelY = imageHeight - 94;
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

        fractionalBiomass.render(guiGraphics, leftPos, topPos, menu.getFractionalBiomass());
    }

}
