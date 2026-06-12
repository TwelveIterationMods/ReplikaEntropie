package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.core.harvester.BiomassHarvesterLogic;
import net.blay09.mods.replikaentropie.menu.BiomassHarvesterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BiomassHarvesterBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {
    public static final int CONTAINER_SIZE = 4;

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            BiomassHarvesterBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return BiomassHarvesterLogic.isValidHarvesterTool(itemStack);
        }
    };
    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, BiomassHarvesterLogic.ENERGY_CAPACITY, BiomassHarvesterLogic.ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            BiomassHarvesterBlockEntity.this.setChanged();
            isSyncDirty = true;
        }
    };
    private final BiomassHarvesterLogic logic = new BiomassHarvesterLogic(backingContainer, energyStorage, this::setChanged, this::sync);
    private boolean isSyncDirty;
    private float clientPrevSpinAngleDeg;
    private float clientSpinAngleDeg;
    private float clientSpinSpeedDegPerSec;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case BiomassHarvesterMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case BiomassHarvesterMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return BiomassHarvesterMenu.DATA_COUNT;
        }
    };

    public BiomassHarvesterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.biomassHarvester.value(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.biomass_harvester");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BiomassHarvesterMenu(containerId, inventory, backingContainer, dataAccess,
                menuPlayer -> energyStorage.fill(menuPlayer.isCreative() ? Integer.MAX_VALUE : 250, false));
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomassHarvesterBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        blockEntity.logic.serverTick(level, Vec3.atCenterOf(pos), pos);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BiomassHarvesterBlockEntity blockEntity) {
        blockEntity.logic.clientTick(level, Vec3.atCenterOf(pos));
        blockEntity.updateClientAnimation();
    }

    private void broadcastChanges() {
        if (isSyncDirty) {
            sync();
            isSyncDirty = false;
        }
    }

    private void sync() {
        BalmBlockEntityUtils.sync(this);
    }

    private void updateClientAnimation() {
        clientPrevSpinAngleDeg = clientSpinAngleDeg;
        final var delta = 1f / 20f;
        final var maxSpeed = 360f * 2f;
        final var accelerationSpeed = 0.045f;
        final var decelerationSpeed = 0.09f;
        switch (logic.getState()) {
            case SLAUGHTERING -> {
                clientSpinSpeedDegPerSec += (maxSpeed - clientSpinSpeedDegPerSec) * accelerationSpeed;
                clientSpinAngleDeg += clientSpinSpeedDegPerSec * delta;
            }
            case COOLING, WARNING, IDLE -> {
                clientSpinSpeedDegPerSec += (0f - clientSpinSpeedDegPerSec) * decelerationSpeed;
                clientSpinAngleDeg += clientSpinSpeedDegPerSec * delta;
            }
        }
        clientSpinAngleDeg = Mth.positiveModulo(clientSpinAngleDeg, 360f);
        clientPrevSpinAngleDeg = Mth.positiveModulo(clientPrevSpinAngleDeg, 360f);
    }

    public float getClientSpinDegrees(float partialTick) {
        return clientPrevSpinAngleDeg + Mth.wrapDegrees(clientSpinAngleDeg - clientPrevSpinAngleDeg) * partialTick;
    }

    public static boolean isValidHarvesterTool(ItemStack itemStack) {
        return BiomassHarvesterLogic.isValidHarvesterTool(itemStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        logic.load(input);
        energyStorage.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        logic.save(output);
        energyStorage.serialize(output);
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

    public Container getWeaponsContainer() {
        return backingContainer;
    }

    public BiomassHarvesterLogic.State getState() {
        return logic.getState();
    }

    public int getStateTicks() {
        return logic.getStateTicks();
    }

    public void restoreState(BiomassHarvesterLogic.State state, int stateTicks) {
        logic.restoreState(state, stateTicks);
    }
}
