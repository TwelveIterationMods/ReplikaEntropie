package net.blay09.mods.replikaentropie.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class EnergyBar {
    private static final Identifier ENERGY_BAR = id("energy_bar");
    private static final Identifier ENERGY_BAR_EMPTY = id("energy_bar_empty");

    public static final int WIDTH = 16;
    public static final int HEIGHT = 82;

    private final int x;
    private final int y;

    public EnergyBar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphicsExtractor graphics, int leftPos, int topPos, float progress) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_EMPTY, leftPos + x, topPos + y, WIDTH, HEIGHT);

        final var energyHeight = (int) (progress * HEIGHT);
        if (energyHeight > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR, leftPos + x, topPos + y - 1 + HEIGHT - energyHeight, WIDTH, energyHeight);
        }
    }
}
