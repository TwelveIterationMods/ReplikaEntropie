package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.capabilities.CommonCapabilities;
import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.menu.FragmentalGeneratorMenu;
import net.blay09.mods.replikaentropie.recipe.FragmentalGeneratorRecipe;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public class FragmentalGeneratorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    public static final int CONTAINER_SIZE = 12;
    private static final int MAX_ENERGY_DRAIN = 1000;
    public static final int MIN_TEMPERATURE = 0;
    public static final int MAX_TEMPERATURE = 100;
    public static final int IDEAL_TEMPERATURE = 50;
    private static final float PASSIVE_COOLING_TEMPERATURE_PER_TICK = 0.1f;

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            FragmentalGeneratorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };

    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(10000, 0, MAX_ENERGY_DRAIN) {
        @Override
        public void setChanged() {
            FragmentalGeneratorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }
    };

    private final int[] processingTicks = new int[CONTAINER_SIZE];
    private final int[] maxProcessingTicks = new int[CONTAINER_SIZE];
    private float temperature;

    private final float[] clientPrevProgress = new float[CONTAINER_SIZE];
    private final float[] clientProgress = new float[CONTAINER_SIZE];
    private final float[] clientItemRotation = new float[CONTAINER_SIZE];
    private boolean isSyncDirty;
    private int ticksSinceSync;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= FragmentalGeneratorMenu.DATA_PROCESSING_TIME_START && index <= FragmentalGeneratorMenu.DATA_PROCESSING_TIME_END) {
                return processingTicks[index - FragmentalGeneratorMenu.DATA_PROCESSING_TIME_START];
            }

            if (index >= FragmentalGeneratorMenu.DATA_MAX_PROCESSING_TIME_START && index <= FragmentalGeneratorMenu.DATA_MAX_PROCESSING_TIME_END) {
                return maxProcessingTicks[index - FragmentalGeneratorMenu.DATA_MAX_PROCESSING_TIME_START];
            }

            return switch (index) {
                case FragmentalGeneratorMenu.DATA_TEMPERATURE ->
                        Mth.clamp(Math.round(temperature), MIN_TEMPERATURE, MAX_TEMPERATURE);
                case FragmentalGeneratorMenu.DATA_MAX_TEMPERATURE -> MAX_TEMPERATURE;
                case FragmentalGeneratorMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case FragmentalGeneratorMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return FragmentalGeneratorMenu.DATA_COUNT;
        }
    };

    public FragmentalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.fragmentalGenerator.value(), blockPos, blockState);
        Arrays.fill(maxProcessingTicks, 0);
        Arrays.fill(clientItemRotation, -1f);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentalGeneratorBlockEntity blockEntity) {
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            if (blockEntity.canStartProcessing(i)) {
                blockEntity.processingTicks[i] = 0;
                blockEntity.maxProcessingTicks[i] = 60;
                blockEntity.setChanged();
            } else if (blockEntity.maxProcessingTicks[i] > 0) {
                blockEntity.processingTicks[i]++;
                blockEntity.applyProcessingTemperature(i);

                if (blockEntity.processingTicks[i] >= blockEntity.maxProcessingTicks[i]) {
                    blockEntity.completeProcessing(i);
                    blockEntity.setChanged();
                }
            }
        }

        blockEntity.coolDownIfIdle();
        blockEntity.pushEnergy(level, pos.above(), Direction.DOWN);
        blockEntity.pushEnergy(level, pos.below(), Direction.UP);
        blockEntity.broadcastChanges();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, FragmentalGeneratorBlockEntity blockEntity) {
        blockEntity.updateClientProgress();
    }

    private boolean canStartProcessing(int slot) {
        final var inputStack = backingContainer.getItem(slot);
        if (inputStack.isEmpty() || maxProcessingTicks[slot] > 0) {
            return false;
        }

        final var recipe = FragmentalGeneratorRecipe.getRecipe(level, inputStack).orElse(null);
        if (recipe == null) {
            return false;
        }

        if (recipe.energy() > 0 && energyStorage.getEnergy() >= energyStorage.getCapacity()) {
            return false;
        }

        return recipe.energy() > 0 || recipe.temperature() != 0f;
    }

    private void completeProcessing(int slot) {
        final var inputStack = backingContainer.getItem(slot);
        final var recipe = FragmentalGeneratorRecipe.getRecipe(level, inputStack).orElse(null);
        if (recipe != null) {
            final var energyProduced = Mth.floor(recipe.energy() * getEfficiencyForTemperature(temperature));
            if (energyProduced > 0) {
                energyStorage.setEnergy(Mth.clamp(energyStorage.getEnergy() + energyProduced, 0, energyStorage.getCapacity()));
            }
            inputStack.shrink(1);
        }

        processingTicks[slot] = 0;
        maxProcessingTicks[slot] = 0;
        isSyncDirty = true;
    }

    private boolean isProcessing() {
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            if (maxProcessingTicks[i] > 0) {
                return true;
            }
        }

        return false;
    }

    private void coolDownIfIdle() {
        if (!isProcessing()) {
            adjustTemperature(-PASSIVE_COOLING_TEMPERATURE_PER_TICK);
        }
    }

    private void broadcastChanges() {
        ticksSinceSync++;

        if (isSyncDirty || (ticksSinceSync >= 10 && isProcessing())) {
            BalmBlockEntityUtils.sync(this);
            isSyncDirty = false;
            ticksSinceSync = 0;
        }
    }

    private void updateClientProgress() {
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            clientPrevProgress[i] = clientProgress[i];
            if (maxProcessingTicks[i] > 0) {
                if (clientItemRotation[i] == -1f) {
                    clientItemRotation[i] = (float) (Math.random() * 360f);
                }
                final var progressPerTick = 1 / (float) maxProcessingTicks[i];
                clientProgress[i] = Mth.clamp(clientProgress[i] + progressPerTick, 0f, 1f);

                final var actualProgress = Mth.clamp(processingTicks[i] / (float) maxProcessingTicks[i], 0f, 1f);
                clientProgress[i] += (actualProgress - clientProgress[i]) * 0.1f;
            } else {
                clientPrevProgress[i] = 0f;
                clientProgress[i] = 0f;
                clientItemRotation[i] = -1f;
            }
        }
    }

    public float getClientProcessingProgress(int index, float partialTick) {
        final var prev = clientPrevProgress[index];
        final var curr = clientProgress[index];
        return prev + (curr - prev) * partialTick;
    }

    public float getClientItemRotation(int index) {
        return clientItemRotation[index];
    }

    private void applyProcessingTemperature(int slot) {
        final var inputStack = backingContainer.getItem(slot);
        final var recipe = FragmentalGeneratorRecipe.getRecipe(level, inputStack).orElse(null);
        if (recipe != null) {
            adjustTemperature(recipe.temperature());
        }
    }

    public boolean adjustTemperature(float delta) {
        final float newTemperature = Mth.clamp(temperature + delta, MIN_TEMPERATURE, MAX_TEMPERATURE);
        if (newTemperature != temperature) {
            temperature = newTemperature;
            setChanged();
            return true;
        }
        return false;
    }

    private static float getEfficiencyForTemperature(float temperature) {
        return Math.max(0.1f, 1f - Math.abs(temperature - IDEAL_TEMPERATURE) / IDEAL_TEMPERATURE);
    }

    private void pushEnergy(Level level, BlockPos targetPos, Direction targetSide) {
        if (energyStorage.getEnergy() <= 0) {
            return;
        }

        final var targetBlockEntity = level.getBlockEntity(targetPos);
        final var targetStorage = targetBlockEntity != null
                ? Balm.capabilities().getCapability(targetBlockEntity, targetSide, CommonCapabilities.ENERGY_STORAGE)
                : null;
        if (targetStorage == null || !targetStorage.canFill()) {
            return;
        }

        final int maxTransfer = Math.min(MAX_ENERGY_DRAIN, energyStorage.getEnergy());
        final int accepted = targetStorage.fill(maxTransfer, true);
        if (accepted <= 0) {
            return;
        }

        final int drained = energyStorage.drain(accepted, false);
        if (drained > 0) {
            targetStorage.fill(drained, false);
        }
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.fragmental_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FragmentalGeneratorMenu(containerId, playerInventory, backingContainer, data);
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
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());

        for (int i = 0; i < CONTAINER_SIZE; i++) {
            output.putInt("ProcessingTicks" + i, processingTicks[i]);
            output.putInt("MaxProcessingTicks" + i, maxProcessingTicks[i]);
        }
        output.putFloat("Temperature", temperature);
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

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());

        for (int i = 0; i < CONTAINER_SIZE; i++) {
            processingTicks[i] = input.getIntOr("ProcessingTicks" + i, 0);
            maxProcessingTicks[i] = input.getIntOr("MaxProcessingTicks" + i, 0);
        }
        temperature = Mth.clamp(input.getFloatOr("Temperature", 0f), MIN_TEMPERATURE, MAX_TEMPERATURE);
        energyStorage.deserialize(input);
    }
}
