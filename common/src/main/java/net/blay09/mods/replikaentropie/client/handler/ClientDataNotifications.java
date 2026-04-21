package net.blay09.mods.replikaentropie.client.handler;

import net.blay09.mods.balm.client.platform.event.callback.RenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    public static void initialize() {
        RenderCallback.Gui.AFTER.register(((graphics, _) -> {
            final var minecraft = Minecraft.getInstance();
            if (!HandheldAnalyzerClient.isOverlayVisible()) {
                final var x = graphics.guiWidth() / 2;
                final var y = graphics.guiHeight() - 62;
                render(graphics, x, y, minecraft.getDeltaTracker().getGameTimeDeltaTicks(), true);
            }
        }));
    }

    public static void render(GuiGraphicsExtractor guiGraphics, int x, int y, float delta, boolean centered) {
        if (dataCollectedTimeLeft > 0) {
            final var alpha = (int) Math.floor(Mth.clamp(dataCollectedTimeLeft / DATA_COLLECTED_TIME, 0f, 1f) * 255);
            if (alpha > 10) {
                final var text = Component.translatable("gui.replikaentropie.data_collected", dataCollected);
                final var font = Minecraft.getInstance().font;
                final var width = font.width(text);
                guiGraphics.text(font, text, x - (centered ? width / 2 : 0), y, 0x55FF55 | (alpha << 24));
            }
            dataCollectedTimeLeft -= delta;
        }
    }
}