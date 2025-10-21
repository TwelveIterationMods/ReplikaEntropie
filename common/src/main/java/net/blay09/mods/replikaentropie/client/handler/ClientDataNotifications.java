package net.blay09.mods.replikaentropie.client.handler;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ClientDataNotifications {

    private static final float DATA_COLLECTED_TIME = 30f;
    private static int dataCollected;
    private static float dataCollectedTimeLeft;

    public static void onDataCollected(int dataCollected) {
        ClientDataNotifications.dataCollected = dataCollected;
        dataCollectedTimeLeft = DATA_COLLECTED_TIME;
    }

    public static void initialize(BalmEvents events) {
        events.onEvent(GuiDrawEvent.Pre.class, event -> {
            if (event.getElement() == GuiDrawEvent.Element.ALL) {
                final var minecraft = Minecraft.getInstance();
                if (!HandheldAnalyzerClient.isOverlayVisible()) {
                    final var guiGraphics = event.getGuiGraphics();
                    final var x = guiGraphics.guiWidth() / 2;
                    final var y = guiGraphics.guiHeight() - 62;
                    render(guiGraphics, x, y, minecraft.getDeltaFrameTime(), true);
                }
            }
        });
    }

    public static void render(GuiGraphics guiGraphics, int x, int y, float delta, boolean centered) {
        if (dataCollectedTimeLeft > 0) {
            final var alpha = (int) Math.floor(Mth.clamp(dataCollectedTimeLeft / DATA_COLLECTED_TIME, 0f, 1f) * 255);
            if (alpha > 10) {
                final var text = Component.translatable("gui.replikaentropie.data_collected", dataCollected);
                final var font = Minecraft.getInstance().font;
                final var width = font.width(text);
                guiGraphics.drawString(font, text, x - (centered ? width / 2 : 0), y, 0x55FF55 | (alpha << 24));
            }
            dataCollectedTimeLeft -= delta;
        }
    }
}