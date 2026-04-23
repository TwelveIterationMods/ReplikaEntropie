package net.blay09.mods.replikaentropie.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IconButton extends Button.Plain {
    private final Identifier iconTexture;
    private final int iconU;
    private final int iconV;
    private final int iconWidth;
    private final int iconHeight;

    public IconButton(Identifier iconTexture, int iconU, int iconV, int iconWidth, int iconHeight, int x, int y, int width, int height, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, Component.empty(), onPress, createNarration);
        this.iconTexture = iconTexture;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        final var iconX = getX() + (width - iconWidth) / 2;
        final var iconY = getY() + (height - iconHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, iconTexture, iconX, iconY, iconU, iconV, iconWidth, iconHeight, 256, 256);
    }
}
