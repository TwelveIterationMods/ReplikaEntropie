package net.blay09.mods.replikaentropie.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {
    private final ResourceLocation iconTexture;
    private final int iconU;
    private final int iconV;
    private final int iconWidth;
    private final int iconHeight;

    public IconButton(ResourceLocation iconTexture, int iconU, int iconV, int iconWidth, int iconHeight, int x, int y, int width, int height, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, Component.empty(), onPress, createNarration);
        this.iconTexture = iconTexture;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        final var iconX = getX() + (width - iconWidth) / 2;
        final var iconY = getY() + (height - iconHeight) / 2;
        guiGraphics.blit(iconTexture, iconX, iconY, iconU, iconV, iconWidth, iconHeight);
    }
}
