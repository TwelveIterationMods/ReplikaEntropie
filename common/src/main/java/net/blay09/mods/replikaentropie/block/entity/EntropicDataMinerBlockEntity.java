package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.menu.EntropicDataMinerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntropicDataMinerBlockEntity extends BlockEntity implements BalmMenuProvider<EntropicDataMinerMenu.Data> {

    private final List<DataMinedEvent> capturedEvents = new ArrayList<>();
    private final Set<String> capturedEventKeys = new HashSet<>();

    public EntropicDataMinerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public EntropicDataMinerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.entropicDataMiner.value(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.entropic_data_miner");
    }

    private EntropicDataMinerMenu.Data createMenuData(Player player) {
        return new EntropicDataMinerMenu.Data(capturedEvents, capturedEvents.stream().filter(it -> Analyzer.isDataMinedEventDownloaded(player, it)).map(DataMinedEvent::asKey).collect(Collectors.toSet()));
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new EntropicDataMinerMenu(containerId, createMenuData(player), ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    public EntropicDataMinerMenu.Data getScreenOpeningData(ServerPlayer player) {
        return createMenuData(player);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntropicDataMinerMenu.Data> getScreenStreamCodec() {
        return EntropicDataMinerMenu.Data.STREAM_CODEC;
    }

    public void addEvent(DataMinedEvent event) {
        if (!capturedEventKeys.contains(event.asKey())) {
            capturedEvents.add(event);
            capturedEventKeys.add(event.asKey());
        }
        setChanged();
    }

    public int getEventCount() {
        return capturedEvents.size();
    }

    public int countChaosEvents() {
        return (int) capturedEvents.stream()
                .filter(it -> it.type() == DataMinedEvent.Type.CHAOS)
                .count();
    }

    public void removeEvent(String key) {
        for (int i = 0; i < capturedEvents.size(); i++) {
            final var event = capturedEvents.get(i);
            if (key.equals(event.asKey())) {
                capturedEvents.remove(i);
                capturedEventKeys.remove(key);
                setChanged();
                break;
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        capturedEvents.clear();
        capturedEventKeys.clear();
        final var list = input.listOrEmpty("Events", DataMinedEvent.CODEC);
        for (final var event : list) {
            capturedEvents.add(event);
            capturedEventKeys.add(event.asKey());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        final var list = output.list("Events", DataMinedEvent.CODEC);
        for (final var event : capturedEvents) {
            list.add(event);
        }
    }

}
