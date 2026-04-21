package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.EntropicDataMinerBlockEntity;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class EntropicDataMinerMenu extends AbstractContainerMenu {

    public record Data(List<DataMinedEvent> events, Set<String> downloadedEvents) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                EventDownloadState.STREAM_CODEC.apply(ByteBufCodecs.list()),
                Data::eventDownloadStates,
                Data::fromEventDownloadStates
        );

        private static Data fromEventDownloadStates(List<EventDownloadState> eventStates) {
            final var events = new ArrayList<DataMinedEvent>();
            final var downloadedEvents = new HashSet<String>();
            for (final var eventState : eventStates) {
                events.add(eventState.event());
                if (eventState.downloaded()) {
                    downloadedEvents.add(eventState.event().asKey());
                }
            }
            return new Data(events, downloadedEvents);
        }

        private List<EventDownloadState> eventDownloadStates() {
            return events.stream()
                    .map(event -> new EventDownloadState(event, downloadedEvents.contains(event.asKey())))
                    .toList();
        }
    }

    private record EventDownloadState(DataMinedEvent event, boolean downloaded) {
        private static final StreamCodec<RegistryFriendlyByteBuf, EventDownloadState> STREAM_CODEC = StreamCodec.composite(
                DataMinedEvent.STREAM_CODEC,
                EventDownloadState::event,
                ByteBufCodecs.BOOL,
                EventDownloadState::downloaded,
                EventDownloadState::new
        );
    }

    protected final ContainerLevelAccess access;
    private final List<DataMinedEvent> events = new ArrayList<>();
    private final Set<String> downloadedEvents = new HashSet<>();

    public EntropicDataMinerMenu(int containerId, Data data) {
        this(containerId, data, ContainerLevelAccess.NULL);
    }

    public EntropicDataMinerMenu(int containerId, Data data, ContainerLevelAccess access) {
        super(ModMenus.entropicDataMiner.value(), containerId);
        this.access = access;
        events.addAll(data.events);
        events.sort(Comparator.comparing(DataMinedEvent::timestamp).reversed());
        downloadedEvents.addAll(data.downloadedEvents);
    }

    public List<DataMinedEvent> getEvents() {
        return events;
    }

    public boolean isDownloaded(DataMinedEvent event) {
        return downloadedEvents.contains(event.asKey());
    }

    public void markDownloaded(DataMinedEvent event) {
        downloadedEvents.add(event.asKey());
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId < 0 || buttonId >= events.size()) {
            return false;
        }

        final var event = events.get(buttonId);
        if (Analyzer.isDataMinedEventDownloaded(player, event)) {
            return false;
        }

        Analyzer.downloadDataMinedEvent(player, event);
        downloadedEvents.add(event.asKey());

        // Chaos events are removed on first download because Chaos Engine only generates with < 5
        if (event.type() == DataMinedEvent.Type.CHAOS) {
            access.execute((level, pos) -> {
                if (level.getBlockEntity(pos) instanceof EntropicDataMinerBlockEntity blockEntity) {
                    blockEntity.removeEvent(event.asKey());
                }
            });
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).is(ModBlocks.entropicDataMiner) && player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5F, pos.getZ() + 0.5f) <= 64f, true);
    }
}
