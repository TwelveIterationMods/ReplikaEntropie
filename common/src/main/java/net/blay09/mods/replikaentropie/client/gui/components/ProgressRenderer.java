package net.blay09.mods.replikaentropie.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;

public interface ProgressRenderer {
    int getLength();

    void render(GuiGraphics guiGraphics, int x, int y, float progress);
}

