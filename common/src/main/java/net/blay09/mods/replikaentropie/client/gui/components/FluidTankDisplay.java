package net.blay09.mods.replikaentropie.client.gui.components;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class FluidTankDisplay {
    private final ProgressRenderer renderer;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Component fluidName;

    public FluidTankDisplay(ProgressRenderer renderer, int x, int y, int width, int height, Component fluidName) {
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fluidName = fluidName;
    }

    public void render(GuiGraphicsExtractor graphics, int leftPos, int topPos, float progress) {
        renderer.render(graphics, leftPos, topPos, progress);
    }

    public boolean isHovering(int leftPos, int topPos, int mouseX, int mouseY) {
        final var x = leftPos + this.x;
        final var y = topPos + this.y;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean extractTooltip(GuiGraphicsExtractor graphics, Font font, int leftPos, int topPos, int mouseX, int mouseY, int amount, int capacity) {
        if (isHovering(leftPos, topPos, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.replikaentropie.fluid_tank", fluidName, amount, capacity), mouseX, mouseY);
            return true;
        }

        return false;
    }

    public boolean extractFullTooltip(GuiGraphicsExtractor graphics, Font font, int leftPos, int topPos, int mouseX, int mouseY) {
        if (isHovering(leftPos, topPos, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.replikaentropie.fluid_tank.full", fluidName), mouseX, mouseY);
            return true;
        }

        return false;
    }
}
