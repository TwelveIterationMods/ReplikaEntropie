package net.blay09.mods.replikaentropie.client.gui.components;

import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.event.callback.RenderCallback;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BurstEnergyBarRenderer {

    private static final Identifier BURST_BAR_TEXTURE = id("textures/gui/burst_bar.png");

    private static final int FADE_OUT_DELAY = 40;
    private static final int FADE_OUT_DURATION = 20;

    private static int fullEnergyTickCount = FADE_OUT_DELAY;

    public static void initialize() {
        RenderCallback.Gui.Health.AFTER.register(((graphics, _) -> renderBurstBar(graphics)));

        ClientTickCallback.AFTER.register(client -> {
            if (client.player != null) {
                BurstEnergy.recharge(client.player);
            }
        });
    }

    private static void renderBurstBar(GuiGraphicsExtractor guiGraphics) {
        final var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        final var burstManager = BurstEnergy.getLocalManager();
        final var energy = burstManager.getEnergy(player);
        final var maxEnergy = burstManager.getMaxEnergy();

        // We fade the bar out after a short delay if it's full
        if (energy >= maxEnergy) {
            fullEnergyTickCount++;
        } else {
            fullEnergyTickCount = 0;
        }

        final var alpha = 1f - Mth.clamp(((fullEnergyTickCount - FADE_OUT_DELAY) / (float) FADE_OUT_DURATION), 0f, 1f);
        if (alpha <= 0f) {
            return;
        }

        final var barWidth = 81;
        final var x = guiGraphics.guiWidth() / 2 - barWidth / 2;
        final var y = guiGraphics.guiHeight() - 62;
        guiGraphics.blit(BURST_BAR_TEXTURE, x, y, 0, 0, barWidth, 9, 256, 256);

        final var fillWidth = (int) ((energy / maxEnergy) * 79);
        if (fillWidth > 0) {
            guiGraphics.blit(BURST_BAR_TEXTURE, x + 1, y + 1, 0, 9, fillWidth, 7, 256, 256);
        }
    }
}
