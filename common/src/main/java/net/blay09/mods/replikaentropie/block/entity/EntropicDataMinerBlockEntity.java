package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.ContainerUtils;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.item.DataItem;
import net.blay09.mods.replikaentropie.menu.EntropicDataMinerMenu;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class EntropicDataMinerBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit> {

    public static final int CONTAINER_SIZE = 10;

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
        return new EntropicDataMinerMenu(containerId, inventory, backingContainer, ContainerLevelAccess.create(level, worldPosition));
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

    public void addEvent(DataMinedEvent event) {
        ContainerUtils.insertItem(backingContainer, DataItem.create(event), false);
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
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
