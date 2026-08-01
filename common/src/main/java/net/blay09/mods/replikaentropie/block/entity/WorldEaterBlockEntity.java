package net.blay09.mods.replikaentropie.block.entity;

import com.mojang.serialization.Codec;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.ContainerUtils;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.menu.WorldEaterMenu;
import net.blay09.mods.replikaentropie.network.protocol.ParticleTrailMessage;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldEaterBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    public static final int CONTAINER_SIZE = 6;

    private static final int SCANNING_TICKS = 200;
    private static final int DESTROY_TICKS_PER_DESTROY_SPEED = 20;
    private static final int SCAN_RANGE = 8;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int ENERGY_COST_PER_TICK = 10;

    private enum State {IDLE, SCANNING, DESTROYING}

    private record ScannedBlock(BlockPos pos, BlockState state, ItemStack itemStack) {
    }

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            WorldEaterBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }
    };
    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            WorldEaterBlockEntity.this.setChanged();
        }
    };

    private final DefaultContainer previewContainer = new DefaultContainer(15) {
        @Override
        public void setChanged() {
            isSyncDirty = true;
        }
    };
    private final NonNullList<ItemStack> outputBuffer = NonNullList.create();

    private final Map<Integer, BlockPos> scannedPositions = new HashMap<>();
    private State state = State.IDLE;
    private int stateTicks;
    private int currentDestroySlot = -1;
    private int currentMaxDestroyTicks;

    private boolean isSyncDirty;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case WorldEaterMenu.DATA_SCANNING_TIME -> state == State.SCANNING ? stateTicks : 0;
                case WorldEaterMenu.DATA_MAX_SCANNING_TIME -> SCANNING_TICKS;
                case WorldEaterMenu.DATA_DESTROYING_TIME -> state == State.DESTROYING ? stateTicks : 0;
                case WorldEaterMenu.DATA_MAX_DESTROYING_TIME -> currentMaxDestroyTicks;
                case WorldEaterMenu.DATA_CURRENT_DESTROY_SLOT -> currentDestroySlot;
                case WorldEaterMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case WorldEaterMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return WorldEaterMenu.DATA_COUNT;
        }
    };

    public WorldEaterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.worldEater.value(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.world_eater");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new WorldEaterMenu(containerId, inventory, previewContainer, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition));
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

    public Container getPreviewContainer() {
        return previewContainer;
    }

    private BlockPos getScanCenter() {
        final var facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return worldPosition.relative(facing.getOpposite(), SCAN_RANGE + 1);
    }

    public AABB getScanArea() {
        final var scanCenter = getScanCenter();
        return new AABB(
                scanCenter.getX() - SCAN_RANGE,
                scanCenter.getY() - SCAN_RANGE,
                scanCenter.getZ() - SCAN_RANGE,
                scanCenter.getX() + SCAN_RANGE,
                scanCenter.getY() + SCAN_RANGE,
                scanCenter.getZ() + SCAN_RANGE
        );
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WorldEaterBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.processState(serverLevel);
        }
    }

    private void broadcastChanges() {
        if (isSyncDirty) {
            BalmBlockEntityUtils.sync(this);
            isSyncDirty = false;
        }
    }

    private void processState(ServerLevel level) {
        if (!outputBuffer.isEmpty()) {
            flushOutputBuffer();
            if (!outputBuffer.isEmpty()) {
                return;
            }
        }

        if (energyStorage.getEnergy() < ENERGY_COST_PER_TICK) {
            return;
        }

        energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_TICK);
        stateTicks++;

        switch (state) {
            case SCANNING -> {
                final var maxPreviewSlots = previewContainer.getContainerSize();
                int step = Math.max(1, SCANNING_TICKS / maxPreviewSlots);
                if (stateTicks % step == 0) {
                    int slotToFill = (stateTicks / step) - 1;
                    if (slotToFill >= 0 && slotToFill < maxPreviewSlots && previewContainer.getItem(slotToFill).isEmpty()) {
                        findRandomScannableBlock(level)
                                .ifPresent(scannedBlock -> {
                                    scannedPositions.put(slotToFill, scannedBlock.pos());
                                    previewContainer.setItem(slotToFill, scannedBlock.itemStack());
                                    level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.25f, (float) (0.5 + Math.random()));
                                    sendTrailParticles(level, Vec3.atCenterOf(scannedBlock.pos()), Vec3.atCenterOf(worldPosition));
                                });
                    }
                }

                if (stateTicks >= SCANNING_TICKS) {
                    if (prepareNextDestroySlot(level)) {
                        transition(State.DESTROYING);
                    } else {
                        scannedPositions.clear();
                        transition(State.SCANNING);
                    }
                }
            }
            case DESTROYING -> {
                if (currentMaxDestroyTicks <= 0) {
                    currentMaxDestroyTicks = getDestroyTicksForSlot(level, currentDestroySlot);
                }

                if (stateTicks >= currentMaxDestroyTicks) {
                    final var maxPreviewSlots = previewContainer.getContainerSize();
                    if (currentDestroySlot >= 0 && currentDestroySlot < maxPreviewSlots) {
                        final var blockItem = previewContainer.getItem(currentDestroySlot);
                        if (!blockItem.isEmpty() && currentDestroySlot < scannedPositions.size()) {
                            final var targetPos = scannedPositions.get(currentDestroySlot);
                            final var targetState = level.getBlockState(targetPos);
                            if (isQuestionablyEdibleBlock(level, targetPos, targetState)) {
                                final var breakSound = targetState.getSoundType().getBreakSound();
                                level.removeBlock(targetPos, false);
                                level.playSound(null, worldPosition, breakSound, SoundSource.BLOCKS, 0.5f, (float) (0.5 + Math.random()));
                                Block.getDrops(targetState, level, targetPos, null)
                                        .forEach(this::insertOrBuffer);
                            }
                            previewContainer.setItem(currentDestroySlot, ItemStack.EMPTY);
                        }
                    }

                    stateTicks = 0;

                    if (!prepareNextDestroySlot(level)) {
                        scannedPositions.clear();
                        transition(State.SCANNING);
                    }

                    setChanged();
                }
            }
            default -> {
                if (prepareNextDestroySlot(level)) {
                    transition(State.DESTROYING);
                } else {
                    scannedPositions.clear();
                    transition(State.SCANNING);
                }
            }
        }
    }

    private void transition(State newState) {
        this.state = newState;
        this.stateTicks = 0;
        setChanged();
    }

    private Optional<ScannedBlock> findRandomScannableBlock(Level level) {
        final var random = level.getRandom();
        final var scanCenter = getScanCenter();
        for (int attempts = 0; attempts < 50; attempts++) {
            final var x = scanCenter.getX() + random.nextInt(-SCAN_RANGE, SCAN_RANGE);
            final var y = scanCenter.getY() + random.nextInt(-SCAN_RANGE, SCAN_RANGE);
            final var z = scanCenter.getZ() + random.nextInt(-SCAN_RANGE, SCAN_RANGE);
            final var targetPos = new BlockPos(x, y, z);
            final var targetState = level.getBlockState(targetPos);
            if (!isQuestionablyEdibleBlock(level, targetPos, targetState)) {
                continue;
            }

            final var blockItem = new ItemStack(targetState.getBlock().asItem());
            return Optional.of(new ScannedBlock(targetPos, targetState, blockItem));
        }

        return Optional.empty();
    }

    private boolean isQuestionablyEdibleBlock(Level level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && !state.is(ModBlockTags.IMMUNE_TO_WORLD_EATER)
                && state.getDestroySpeed(level, pos) >= 0f
                && !state.hasBlockEntity();
    }

    private boolean prepareNextDestroySlot(Level level) {
        final int nextDestroySlot = getNextDestroySlot();
        currentDestroySlot = nextDestroySlot;
        currentMaxDestroyTicks = getDestroyTicksForSlot(level, nextDestroySlot);
        return nextDestroySlot != -1;
    }

    private int getDestroyTicksForSlot(Level level, int destroySlot) {
        if (destroySlot < 0) {
            return 0;
        }

        final var targetPos = scannedPositions.get(destroySlot);
        if (targetPos == null) {
            return 0;
        }

        final var targetState = level.getBlockState(targetPos);
        return getDestroyTicks(level, targetPos, targetState);
    }

    private int getDestroyTicks(Level level, BlockPos pos, BlockState state) {
        final float destroySpeed = Math.max(0f, state.getDestroySpeed(level, pos));
        return Math.max(1, (int) Math.ceil(destroySpeed * DESTROY_TICKS_PER_DESTROY_SPEED));
    }

    private int getNextDestroySlot() {
        for (int i = 0; i < previewContainer.getContainerSize(); i++) {
            if (!previewContainer.getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private void sendTrailParticles(ServerLevel level, Vec3 start, Vec3 end) {
        final var midPoint = start.lerp(end, 0.5);
        Balm.networking().sendToTracking(level, BlockPos.containing(midPoint), new ParticleTrailMessage(start.toVector3f(), end.toVector3f(), 6, ParticleTypes.SMALL_GUST));
    }

    private void insertOrBuffer(ItemStack itemStack) {
        final var remainingItem = ContainerUtils.insertItem(backingContainer, itemStack, false);
        if (!remainingItem.isEmpty()) {
            outputBuffer.add(remainingItem);
            setChanged();
        }
    }

    private void flushOutputBuffer() {
        boolean changed = false;
        for (int i = 0; i < outputBuffer.size(); ) {
            final var remainingItem = ContainerUtils.insertItem(backingContainer, outputBuffer.get(i), false);
            if (remainingItem.isEmpty()) {
                outputBuffer.remove(i);
                changed = true;
            } else {
                outputBuffer.set(i, remainingItem);
                i++;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        try {
            state = input.getString("State").map(State::valueOf).orElse(State.IDLE);
        } catch (IllegalArgumentException e) {
            state = State.IDLE;
        }
        stateTicks = input.getIntOr("StateTicks", 0);
        currentDestroySlot = input.getIntOr("CurrentDestroySlot", -1);
        currentMaxDestroyTicks = input.getIntOr("CurrentMaxDestroyTicks", 0);

        scannedPositions.clear();
        final var scannedPositionsArray = input.listOrEmpty("ScannedPositions", Codec.LONG);
        int i = 0;
        for (final var longPos : scannedPositionsArray) {
            scannedPositions.put(i, BlockPos.of(longPos));
            i++;
        }

        previewContainer.getItems().clear();
        input.child("Preview").ifPresent(child -> ContainerHelper.loadAllItems(child, previewContainer.getItems()));

        outputBuffer.clear();
        input.child("OutputBuffer").ifPresent(child -> ContainerHelper.loadAllItems(child, outputBuffer));
        energyStorage.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putString("State", state.name());
        output.putInt("StateTicks", stateTicks);
        output.putInt("CurrentDestroySlot", currentDestroySlot);
        output.putInt("CurrentMaxDestroyTicks", currentMaxDestroyTicks);
        final var scannedPositionsArray = output.list("ScannedPositions", Codec.LONG);
        scannedPositions.values().stream().map(BlockPos::asLong).forEach(scannedPositionsArray::add);
        ContainerHelper.saveAllItems(output.child("Preview"), previewContainer.getItems());
        ContainerHelper.saveAllItems(output.child("OutputBuffer"), outputBuffer);
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

}
