package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.FragmentalGeneratorMenu;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.blay09.mods.replikaentropie.util.FractionalResource;
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
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public class FragmentalGeneratorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    public static final int CONTAINER_SIZE = 12;
    private static final int MIN_PROCESSING_TICKS = 60;
    private static final int MAX_PROCESSING_TICKS = 140;
    private static final int INPUTS_COUNT = 4;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_OUTPUT_RATE = 1000;
    private static final int ENERGY_PER_FRAGMENT = 1000;
    public static final float OUTPUT_MULTIPLIER = 2f;

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            FragmentalGeneratorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return inputContainer.containsOuterSlot(slot);
        }
    };

    private final SubContainer inputContainer = new SubContainer(backingContainer, 0, 4);
    private final SubContainer outputContainer = new SubContainer(backingContainer, 4, 8);
    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, 0, ENERGY_OUTPUT_RATE) {
        @Override
        public void setChanged() {
            FragmentalGeneratorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }
    };

    private final int[] processingTicks = new int[INPUTS_COUNT];
    private final int[] maxProcessingTicks = new int[INPUTS_COUNT];
    private final FractionalResource[] fragments = new FractionalResource[]{
            new FractionalResource(outputContainer, 0, ModItems.fragments),
            new FractionalResource(outputContainer, 1, ModItems.fragments),
            new FractionalResource(outputContainer, 2, ModItems.fragments),
            new FractionalResource(outputContainer, 3, ModItems.fragments)
    };

    private final float[] clientPrevProgress = new float[INPUTS_COUNT];
    private final float[] clientProgress = new float[INPUTS_COUNT];
    private final float[] clientItemRotation = new float[INPUTS_COUNT];
    private boolean isSyncDirty;
    private int ticksSinceSync;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0, 1, 2, 3 -> processingTicks[index];
                case 4, 5, 6, 7 -> maxProcessingTicks[index - 4];
                case 8, 9, 10, 11 -> fragments[index - 8].getFractionalAmountAsMenuData();
                case 12 -> energyStorage.getEnergy();
                case 13 -> energyStorage.getCapacity();
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
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentalGeneratorBlockEntity blockEntity) {
        for (int i = 0; i < INPUTS_COUNT; i++) {
            if (blockEntity.canStartProcessing(i)) {
                blockEntity.processingTicks[i] = 0;
                blockEntity.maxProcessingTicks[i] = level.getRandom().nextInt(MIN_PROCESSING_TICKS, MAX_PROCESSING_TICKS);
                blockEntity.setChanged();
            } else if (blockEntity.maxProcessingTicks[i] > 0) {
                blockEntity.processingTicks[i]++;

                if (blockEntity.processingTicks[i] >= blockEntity.maxProcessingTicks[i]) {
                    blockEntity.completeProcessing(i);
                    blockEntity.setChanged();
                }
            }
        }
        blockEntity.broadcastChanges();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, FragmentalGeneratorBlockEntity blockEntity) {
        blockEntity.updateClientProgress();
    }

    private boolean canStartProcessing(int slot) {
        return RecyclerRecipe.getRecipe(level, inputContainer.getItem(slot))
                .filter(it -> it.fragments() > 0)
                .map(it -> maxProcessingTicks[slot] == 0 && fragments[slot].hasSpace())
                .orElse(false);
    }

    private void completeProcessing(int slot) {
        final var inputStack = backingContainer.getItem(slot);
        if (!inputStack.isEmpty()) {
            RecyclerRecipe.getRecipe(level, inputStack)
                    .filter(it -> it.fragments() > 0)
                    .ifPresent(it -> {
                        final var fragmentOutput = it.fragments() * OUTPUT_MULTIPLIER;
                        fragments[slot].add(fragmentOutput);
                        energyStorage.fill((int) (fragmentOutput * ENERGY_PER_FRAGMENT), false);
                    });
            inputStack.shrink(1);
        }

        processingTicks[slot] = 0;
        maxProcessingTicks[slot] = 0;
        isSyncDirty = true;
    }

    private boolean isProcessing() {
        for (int i = 0; i < INPUTS_COUNT; i++) {
            if (maxProcessingTicks[i] > 0) {
                return true;
            }
        }

        return false;
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
        for (int i = 0; i < INPUTS_COUNT; i++) {
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

    public Container getInputContainer() {
        return inputContainer;
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public Container getContainer(Direction side) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (side) {
            case DOWN -> outputContainer;
            default -> backingContainer;
        };
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

        for (int i = 0; i < INPUTS_COUNT; i++) {
            output.putInt("ProcessingTicks" + i, processingTicks[i]);
            output.putInt("MaxProcessingTicks" + i, maxProcessingTicks[i]);
            output.putFloat("FractionalFragments" + i, fragments[i].getFractionalAmount());
        }
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

        for (int i = 0; i < INPUTS_COUNT; i++) {
            processingTicks[i] = input.getIntOr("ProcessingTicks" + i, 0);
            maxProcessingTicks[i] = input.getIntOr("MaxProcessingTicks" + i, 0);
            fragments[i].setFractionalAmount(input.getFloatOr("FractionalFragments" + i, 0f));
        }
        energyStorage.deserialize(input);
    }
}
