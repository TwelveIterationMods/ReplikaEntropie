package net.blay09.mods.replikaentropie.client.gui.components;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BurstEnergyBarRenderer {

    private static final ResourceLocation BURST_BAR_TEXTURE = id("textures/gui/burst_bar.png");

    private static final int FADE_OUT_DELAY = 40;
    private static final int FADE_OUT_DURATION = 20;

    private static int fullEnergyTickCount = FADE_OUT_DELAY;

    public static void initialize(BalmEvents events) {
        events.onEvent(GuiDrawEvent.Post.class, event -> {
            if (event.getElement() == GuiDrawEvent.Element.HEALTH) {
                renderBurstBar(event.getGuiGraphics());
            }
        });

        events.onTickEvent(TickType.Client, TickPhase.End, (client) -> {
            if (client != null && client.player != null) {
                BurstEnergy.recharge(client.player);
            }
        });
    }

    private static void renderBurstBar(GuiGraphics guiGraphics) {
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
        guiGraphics.setColor(1f, 1f, 1f, alpha);
        guiGraphics.blit(BURST_BAR_TEXTURE, x, y, 0, 0, barWidth, 9, 256, 256);

        final var fillWidth = (int) ((energy / maxEnergy) * 79);
        if (fillWidth > 0) {
            guiGraphics.blit(BURST_BAR_TEXTURE, x + 1, y + 1, 0, 9, fillWidth, 7, 256, 256);
        }

        guiGraphics.setColor(1f, 1f, 1f, 1f);
    }
}
