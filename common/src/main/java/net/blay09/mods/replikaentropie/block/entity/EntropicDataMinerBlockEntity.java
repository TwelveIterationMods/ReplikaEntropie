package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.menu.EntropicDataMinerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntropicDataMinerBlockEntity extends BlockEntity implements BalmMenuProvider {

    private final List<DataMinedEvent> capturedEvents = new ArrayList<>();
    private final Set<String> capturedEventKeys = new HashSet<>();

    public EntropicDataMinerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public EntropicDataMinerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.entropicDataMiner.get(), pos, blockState);
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
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        createMenuData(player).write(buf);
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
    public void load(CompoundTag tag) {
        super.load(tag);

        capturedEvents.clear();
        capturedEventKeys.clear();
        final var list = tag.getList("Events", Tag.TAG_COMPOUND);
        for (final var item : list) {
            if (item instanceof CompoundTag itemCompound) {
                final var event = DataMinedEvent.of(itemCompound);
                capturedEvents.add(event);
                capturedEventKeys.add(event.asKey());
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        final var list = new ListTag();
        for (final var event : capturedEvents) {
            list.add(event.save(new CompoundTag()));
        }
        tag.put("Events", list);
    }

}
