package net.blay09.mods.replikaentropie.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class EnergyBar {
    private static final Identifier ENERGY_BAR = id("textures/gui/sprites/energy_bar.png");
    private static final Identifier ENERGY_BAR_EMPTY = id("textures/gui/sprites/energy_bar_empty.png");

    public static final int WIDTH = 16;
    public static final int HEIGHT = 82;

    private final int x;
    private final int y;

    public EnergyBar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphicsExtractor graphics, int leftPos, int topPos, float progress) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_EMPTY, leftPos + x, topPos + y, 0f, 0f, WIDTH, HEIGHT, WIDTH, HEIGHT);

        final var energyHeight = (int) (progress * HEIGHT);
        if (energyHeight > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ENERGY_BAR, leftPos + x, topPos + y + HEIGHT - energyHeight, 0f, HEIGHT - energyHeight, WIDTH, energyHeight, WIDTH, HEIGHT);
        }
    }

    public boolean isHovering(int leftPos, int topPos, int mouseX, int mouseY) {
        final var x = leftPos + this.x;
        final var y = topPos + this.y;
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }

    public boolean extractTooltip(GuiGraphicsExtractor graphics, Font font, int leftPos, int topPos, int mouseX, int mouseY, int currentEnergy, int maxEnergy) {
        if (isHovering(leftPos, topPos, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.replikaentropie.energy_bar", currentEnergy, maxEnergy), mouseX, mouseY);
            return true;
        }

        return false;
    }
}
