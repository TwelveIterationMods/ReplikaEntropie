package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.replikaentropie.client.gui.ExtendedGuiGraphics;
import net.blay09.mods.replikaentropie.client.gui.components.IconButton;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.menu.EntropicDataMinerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class EntropicDataMinerScreen extends AbstractContainerScreen<EntropicDataMinerMenu> {

    private static final Identifier TEXTURE = id("textures/gui/container/entropic_data_miner.png");

    private static final int MAX_EVENTS = 5;
    private static final int DOWNLOAD_TICKS = 15;

    private final List<DataMinedEvent> visibleEvents = new ArrayList<>();
    private final Map<DataMinedEvent, Integer> downloadingEvents = new HashMap<>();

    public EntropicDataMinerScreen(EntropicDataMinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 210, 135);
    }

    @Override
    protected void init() {
        super.init();

        refillEvents(Mth.clamp(MAX_EVENTS - visibleEvents.size(), 0, MAX_EVENTS));

        for (int i = 0; i < visibleEvents.size(); i++) {
            final var event = visibleEvents.get(i);
            int y = topPos + 23 + i * 19;
            final var button = new IconButton(TEXTURE, 212, 2, 12, 14, leftPos + 179, y, 19, 18, it -> {
                if (!downloadingEvents.containsKey(event) && !menu.isDownloaded(event)) {
                    downloadingEvents.put(event, 0);
                    it.active = false;
                }
            }, (it) -> Component.translatable("gui.replikaentropie.entropic_data_miner.download"));
            button.active = !menu.isDownloaded(event) && !downloadingEvents.containsKey(event);
            addRenderableWidget(button);
        }
    }

    private void refillEvents(int num) {
        menu.getEvents().stream()
                .filter(event -> !menu.isDownloaded(event) && !visibleEvents.contains(event))
                .limit(num)
                .forEach(visibleEvents::add);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        var completedDownloads = new ArrayList<DataMinedEvent>();
        for (var entry : downloadingEvents.entrySet()) {
            final var event = entry.getKey();
            var progress = entry.getValue();
            progress++;

            if (progress >= DOWNLOAD_TICKS) {
                final var buttonId = menu.getEvents().indexOf(event);
                final var gameMode = Minecraft.getInstance().gameMode;
                if (gameMode != null) {
                    gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
                }
                completedDownloads.add(event);
            } else {
                downloadingEvents.put(event, progress);
            }
        }

        if (!completedDownloads.isEmpty()) {
            completedDownloads.forEach(visibleEvents::remove);
            completedDownloads.forEach(downloadingEvents::remove);
            completedDownloads.forEach(menu::markDownloaded);
            rebuildWidgets();
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        for (int i = 0; i < visibleEvents.size(); i++) {
            final var x = leftPos + 40;
            final var y = topPos + 24 + i * 19;
            final var event = visibleEvents.get(i);
            if (!event.icon().isEmpty()) {
                graphics.item(event.icon(), x, y);
            }

            final var eventLabel = getComponentForEvent(event);
            ExtendedGuiGraphics.renderScrollingString(graphics, font, eventLabel, x + 20, y, x + 20 + 112, y + 18, 0xFF404040);

            if (downloadingEvents.containsKey(event)) {
                final var downloadProgress = downloadingEvents.get(event);
                final var progress = (float) downloadProgress / DOWNLOAD_TICKS;
                final var barX = leftPos + 60;
                final var barY = topPos + 38 + i * 19;
                final var barWidth = 113;
                final var barHeight = 2;

                graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF404040);
                final var progressWidth = (int) (barWidth * progress);
                graphics.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFF00FF00);
            }
        }

        if (visibleEvents.isEmpty()) {
            final var noEventsLabel = Component.translatable("gui.replikaentropie.entropic_data_miner.no_events");
            final var stringWidth = font.width(noEventsLabel);
            graphics.text(font, noEventsLabel, leftPos + 38 + 138 / 2 - stringWidth / 2, topPos + 29, 0xFF404040, false);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, 6, 6, 0xFF404040, false);
    }

    private Component getComponentForEvent(DataMinedEvent event) {
        final var lowerCaseName = event.type().name().toLowerCase();
        return Component.translatable("gui.replikaentropie.entropic_data_miner.event." + lowerCaseName, event.label());
    }

}
