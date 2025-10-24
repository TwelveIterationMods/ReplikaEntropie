package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.DefragmentizerMenu;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.blay09.mods.replikaentropie.util.FractionalResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class DefragmentizerBlockEntity extends BalmBlockEntity implements BalmContainerProvider, BalmMenuProvider {

    private static final int MIN_PROCESSING_TICKS = 60;
    private static final int MAX_PROCESSING_TICKS = 140;
    private static final int INPUTS_COUNT = 4;
    public static final float OUTPUT_MULTIPLIER = 2f;

    private final DefaultContainer backingContainer = new DefaultContainer(8) {
        @Override
        public void setChanged() {
            DefragmentizerBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return inputContainer.containsOuterSlot(slot);
        }
    };

    private final SubContainer inputContainer = new SubContainer(backingContainer, 0, 4);
    private final SubContainer outputContainer = new SubContainer(backingContainer, 4, 8);

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
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DefragmentizerMenu.DATA_COUNT;
        }
    };

    public DefragmentizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.defragmentizer.get(), blockPos, blockState);
        Arrays.fill(maxProcessingTicks, 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DefragmentizerBlockEntity blockEntity) {
        for (int i = 0; i < INPUTS_COUNT; i++) {
            if (blockEntity.canStartProcessing(i)) {
                blockEntity.processingTicks[i] = 0;
                blockEntity.maxProcessingTicks[i] = level.random.nextInt(MIN_PROCESSING_TICKS, MAX_PROCESSING_TICKS);
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

    public static void clientTick(Level level, BlockPos pos, BlockState state, DefragmentizerBlockEntity blockEntity) {
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
                    .ifPresent(it -> fragments[slot].add(it.fragments() * OUTPUT_MULTIPLIER));
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
            sync();
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
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.defragmentizer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DefragmentizerMenu(containerId, playerInventory, backingContainer, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        ContainerHelper.saveAllItems(tag, backingContainer.getItems());

        for (int i = 0; i < INPUTS_COUNT; i++) {
            tag.putInt("ProcessingTicks" + i, processingTicks[i]);
            tag.putInt("MaxProcessingTicks" + i, maxProcessingTicks[i]);
            tag.putFloat("FractionalFragments" + i, fragments[i].getFractionalAmount());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(tag, backingContainer.getItems());

        for (int i = 0; i < INPUTS_COUNT; i++) {
            processingTicks[i] = tag.getInt("ProcessingTicks" + i);
            maxProcessingTicks[i] = tag.getInt("MaxProcessingTicks" + i);
            fragments[i].setFractionalAmount(tag.getFloat("FractionalFragments" + i));
        }
    }

    @Override
    protected void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        for (int i = 0; i < INPUTS_COUNT; i++) {
            tag.putInt("ProcessingTicks" + i, processingTicks[i]);
            tag.putInt("MaxProcessingTicks" + i, maxProcessingTicks[i]);
        }
    }
}
