package net.blay09.mods.replikaentropie.block.entity;

import com.mojang.serialization.Codec;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.ContainerUtils;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.item.DataItem;
import net.blay09.mods.replikaentropie.menu.EntropicDataMinerMenu;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.network.protocol.ParticleTrailMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntropicDataMinerBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    public static final int CONTAINER_SIZE = 1;
    public static final int EVENT_HISTORY_SIZE = 8;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int ENERGY_COST_PER_EVENT = 10;
    private static final String GENERATED_EVENT_KEYS_TAG = "GeneratedEventKeys";
    private static final String RECENT_EVENTS_TAG = "RecentEvents";

    private final Set<String> generatedEventKeys = new HashSet<>();
    private final List<DataMinedEvent> recentEvents = new ArrayList<>(EVENT_HISTORY_SIZE);

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            EntropicDataMinerBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }
    };

    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            EntropicDataMinerBlockEntity.this.setChanged();
        }
    };

    private final DefaultContainer eventHistoryContainer = new DefaultContainer(EVENT_HISTORY_SIZE) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case EntropicDataMinerMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case EntropicDataMinerMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return EntropicDataMinerMenu.DATA_COUNT;
        }
    };

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

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new EntropicDataMinerMenu(containerId, inventory, eventHistoryContainer, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
        return Unit.STREAM_CODEC.cast();
    }

    @Override
    public Unit getScreenOpeningData(ServerPlayer player) {
        return Unit.INSTANCE;
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    public void addEvent(DataMinedEvent event, BlockPos eventPos) {
        final var eventKey = event.asKey();
        if (generatedEventKeys.contains(eventKey) || energyStorage.getEnergy() < ENERGY_COST_PER_EVENT) {
            return;
        }

        final var remainingItem = ContainerUtils.insertItem(backingContainer, DataItem.create(event), false);
        if (remainingItem.isEmpty()) {
            generatedEventKeys.add(eventKey);
            addToEventHistory(event);
            energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_EVENT);
            setChanged();
            playCaptureEffects(eventPos);
        }
    }

    private void addToEventHistory(DataMinedEvent event) {
        recentEvents.addFirst(event);
        if (recentEvents.size() > EVENT_HISTORY_SIZE) {
            recentEvents.removeLast();
        }
        updateEventHistoryContainer();
    }

    private void updateEventHistoryContainer() {
        for (int i = 0; i < EVENT_HISTORY_SIZE; i++) {
            if (i < recentEvents.size()) {
                final var event = recentEvents.get(i);
                final var icon = event.icon().isEmpty() ? DataItem.create(event) : event.icon().copy();
                icon.setCount(1);
                icon.set(DataComponents.CUSTOM_NAME, event.getDisplayName());
                eventHistoryContainer.setItem(i, icon);
            } else {
                eventHistoryContainer.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private void playCaptureEffects(BlockPos eventPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final var start = Vec3.atCenterOf(eventPos);
        final var end = Vec3.atCenterOf(worldPosition);
        final var midPoint = start.lerp(end, 0.5);
        final var message = new ParticleTrailMessage(start.toVector3f(), end.toVector3f(), 12, ParticleTypes.ENCHANT);
        Balm.networking().sendToTracking(serverLevel, BlockPos.containing(midPoint), message);
        serverLevel.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1f);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        energyStorage.deserialize(input);
        generatedEventKeys.clear();
        input.listOrEmpty(GENERATED_EVENT_KEYS_TAG, Codec.STRING).forEach(generatedEventKeys::add);
        recentEvents.clear();
        input.listOrEmpty(RECENT_EVENTS_TAG, DataMinedEvent.CODEC).stream()
                .limit(EVENT_HISTORY_SIZE)
                .forEach(recentEvents::add);
        updateEventHistoryContainer();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        energyStorage.serialize(output);
        final var generatedEventKeysList = output.list(GENERATED_EVENT_KEYS_TAG, Codec.STRING);
        generatedEventKeys.forEach(generatedEventKeysList::add);
        final var recentEventsList = output.list(RECENT_EVENTS_TAG, DataMinedEvent.CODEC);
        recentEvents.forEach(recentEventsList::add);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, this::saveAdditional);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return BalmBlockEntityUtils.createUpdatePacket(this);
    }
}
