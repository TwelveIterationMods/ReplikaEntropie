package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.WorldEaterMenu;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.blay09.mods.replikaentropie.util.FractionalResource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldEaterBlockEntity extends BalmBlockEntity implements BalmContainerProvider, BalmMenuProvider {

    private static final int SCANNING_TICKS = 200;
    private static final int DESTROY_TICKS = 100;
    private static final int SCAN_RANGE = 16;

    private enum State {IDLE, SCANNING, DESTROYING}

    private record ScannedBlock(BlockPos pos, BlockState state, ItemStack itemStack) {
    }

    private final DefaultContainer backingContainer = new DefaultContainer(1) {
        @Override
        public void setChanged() {
            WorldEaterBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }
    };

    private final DefaultContainer previewContainer = new DefaultContainer(15) {
        @Override
        public void setChanged() {
            super.setChanged();
            isSyncDirty = true;
        }
    };

    private final FractionalResource scrap = new FractionalResource(backingContainer, 0, ModItems.scrap);

    private final Map<Integer, BlockPos> scannedPositions = new HashMap<>();
    private State state = State.IDLE;
    private int stateTicks;
    private int currentDestroySlot = -1;

    private boolean isSyncDirty;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case WorldEaterMenu.DATA_SCANNING_TIME -> state == State.SCANNING ? stateTicks : 0;
                case WorldEaterMenu.DATA_MAX_SCANNING_TIME -> SCANNING_TICKS;
                case WorldEaterMenu.DATA_DESTROYING_TIME -> state == State.DESTROYING ? stateTicks : 0;
                case WorldEaterMenu.DATA_MAX_DESTROYING_TIME -> DESTROY_TICKS;
                case WorldEaterMenu.DATA_CURRENT_DESTROY_SLOT -> currentDestroySlot;
                case WorldEaterMenu.DATA_FRACTIONAL_SCRAP -> scrap.getFractionalAmountAsMenuData();
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
        super(ModBlockEntities.worldEater.get(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.world_eater");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new WorldEaterMenu(containerId, inventory, previewContainer, backingContainer, dataAccess);
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    public Container getPreviewContainer() {
        return previewContainer;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WorldEaterBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        blockEntity.processState(level);
    }

    private void broadcastChanges() {
        if (isSyncDirty) {
            sync();
            isSyncDirty = false;
        }
    }

    private void processState(Level level) {
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
                                });
                    }
                }

                if (stateTicks >= SCANNING_TICKS) {
                    currentDestroySlot = getNextDestroySlot();
                    if (currentDestroySlot != -1) {
                        transition(State.DESTROYING);
                    } else {
                        scannedPositions.clear();
                        transition(State.SCANNING);
                    }
                }
            }
            case DESTROYING -> {
                if (!hasSpaceForScrap()) {
                    return;
                }

                if (stateTicks >= DESTROY_TICKS) {
                    final var maxPreviewSlots = previewContainer.getContainerSize();
                    if (currentDestroySlot >= 0 && currentDestroySlot < maxPreviewSlots) {
                        final var blockItem = previewContainer.getItem(currentDestroySlot);
                        if (!blockItem.isEmpty() && currentDestroySlot < scannedPositions.size()) {
                            final var targetPos = scannedPositions.get(currentDestroySlot);
                            final var targetState = level.getBlockState(targetPos);
                            if (isQuestionablyEdibleBlock(level, targetPos, targetState)) {
                                level.removeBlock(targetPos, false);
                                scrap.add(level.random.nextFloat() * 0.5f);
                            }
                            previewContainer.setItem(currentDestroySlot, ItemStack.EMPTY);
                        }
                    }

                    stateTicks = 0;

                    currentDestroySlot = getNextDestroySlot();
                    if (currentDestroySlot == -1) {
                        scannedPositions.clear();
                        transition(State.SCANNING);
                    }

                    setChanged();
                }
            }
            default -> {
                currentDestroySlot = getNextDestroySlot();
                if (currentDestroySlot != -1) {
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

    private boolean hasSpaceForScrap() {
        final var resultSlotItem = backingContainer.getItem(0);
        return resultSlotItem.isEmpty() || resultSlotItem.getCount() < resultSlotItem.getMaxStackSize();
    }

    private Optional<ScannedBlock> findRandomScannableBlock(Level level) {
        final var random = level.getRandom();
        for (int attempts = 0; attempts < 50; attempts++) {
            final var x = worldPosition.getX() + random.nextInt(-SCAN_RANGE, SCAN_RANGE);
            final var y = worldPosition.getY() + random.nextInt(-SCAN_RANGE, SCAN_RANGE);
            final var z = worldPosition.getZ() + random.nextInt(-SCAN_RANGE, SCAN_RANGE);
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

    private int getNextDestroySlot() {
        for (int i = 0; i < previewContainer.getContainerSize(); i++) {
            if (!previewContainer.getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, backingContainer.getItems());
        try {
            state = State.valueOf(tag.getString("State"));
        } catch (IllegalArgumentException e) {
            state = State.IDLE;
        }
        stateTicks = tag.getInt("StateTicks");
        currentDestroySlot = tag.getInt("CurrentDestroySlot");
        scrap.setFractionalAmount(tag.getFloat("FractionalScrap"));

        scannedPositions.clear();
        final var scannedPositionsArray = tag.getLongArray("ScannedPositions");
        for (int i = 0; i < scannedPositionsArray.length; i++) {
            scannedPositions.put(i, BlockPos.of(scannedPositionsArray[i]));
        }

        previewContainer.clearContent();
        final var previewTag = tag.getCompound("Preview");
        ContainerHelper.loadAllItems(previewTag, previewContainer.getItems());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putString("State", state.name());
        tag.putInt("StateTicks", stateTicks);
        tag.putInt("CurrentDestroySlot", currentDestroySlot);
        tag.putFloat("FractionalScrap", scrap.getFractionalAmount());
        tag.putLongArray("ScannedPositions", scannedPositions.values().stream().map(BlockPos::asLong).toList());
    }

    @Override
    protected void writeUpdateTag(CompoundTag tag) {
        final var previewTag = new CompoundTag();
        ContainerHelper.saveAllItems(previewTag, previewContainer.getItems());
        tag.put("Preview", previewTag);
    }
}
