package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.capabilities.CommonCapabilities;
import net.blay09.mods.balm.platform.fluid.DefaultFluidTank;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.blay09.mods.replikaentropie.menu.LavaSinkMenu;

public class LavaSinkBlockEntity extends BlockEntity implements BalmContainerProvider {
    public static final int INPUT_SLOT = 0;
    public static final int BUCKET_SLOT = 1;
    public static final int CONTAINER_SIZE = 2;
    public static final int PROCESSING_TICKS = 50;
    public static final int LAVA_PER_OPERATION = 250;
    public static final int LAVA_CAPACITY = 4000;

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            LavaSinkBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return switch (index) {
                case INPUT_SLOT -> stack.is(Items.COBBLESTONE);
                case BUCKET_SLOT -> stack.is(Items.BUCKET) && getItem(index).isEmpty();
                default -> false;
            };
        }



        @Override
        public int getMaxStackSize(ItemStack itemStack) {
            return itemStack.is(Items.BUCKET) || itemStack.is(Items.LAVA_BUCKET) ? 1 : super.getMaxStackSize(itemStack);
        }

        @Override
        public boolean canTakeItem(Container target, int index, ItemStack stack) {
            return index == BUCKET_SLOT && stack.is(Items.LAVA_BUCKET);
        }
    };
    private final Container bucketOutputContainer = new SubContainer(backingContainer, BUCKET_SLOT, BUCKET_SLOT + 1);
    private final DefaultFluidTank lavaTank = new DefaultFluidTank(LAVA_CAPACITY);
    private int processingTicks;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case LavaSinkMenu.DATA_PROCESSING_TICKS -> processingTicks;
                case LavaSinkMenu.DATA_MAX_PROCESSING_TICKS -> PROCESSING_TICKS;
                case LavaSinkMenu.DATA_LAVA_TANK -> lavaTank.getAmount(0);
                case LavaSinkMenu.DATA_MAX_LAVA_TANK -> lavaTank.getCapacity(0);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return LavaSinkMenu.DATA_COUNT;
        }
    };

    public LavaSinkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.lavaSink.value(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LavaSinkBlockEntity blockEntity) {
        blockEntity.generateLava();
        blockEntity.fillBuckets();
        blockEntity.pushLavaDown(level, pos);
    }

    private void generateLava() {
        if (!canGenerateLava()) {
            if (processingTicks != 0) {
                processingTicks = 0;
                setChanged();
            }
            return;
        }

        processingTicks++;
        if (processingTicks < PROCESSING_TICKS) {
            setChanged();
            return;
        }

        backingContainer.removeItem(INPUT_SLOT, 1);
        lavaTank.fill(0, Fluids.LAVA, LAVA_PER_OPERATION, false);
        processingTicks = 0;
        setChanged();
    }

    private boolean canGenerateLava() {
        final var inputStack = backingContainer.getItem(INPUT_SLOT);
        return inputStack.is(Items.COBBLESTONE) && lavaTank.getAmount(0) + LAVA_PER_OPERATION <= lavaTank.getCapacity(0);
    }

    private void fillBuckets() {
        final var bucketStack = backingContainer.getItem(BUCKET_SLOT);
        if (bucketStack.is(Items.BUCKET) && bucketStack.count() == 1 && lavaTank.getAmount(0) >= 1000) {
            lavaTank.drain(0, Fluids.LAVA, 1000, false);
            backingContainer.setItem(BUCKET_SLOT, new ItemStack(Items.LAVA_BUCKET));
            setChanged();
        }
    }

    private void pushLavaDown(Level level, BlockPos pos) {
        if (lavaTank.isEmpty(0)) {
            return;
        }

        for (BlockPos currentPos = pos.below(); currentPos.getY() >= level.getMinY(); currentPos = currentPos.below()) {
            final var blockEntity = level.getBlockEntity(currentPos);
            if (blockEntity instanceof FragmentalHeaterBlockEntity fragmentalHeaterBlockEntity) {
                if (fragmentalHeaterBlockEntity.adjustTemperature(0.1f)) {
                    lavaTank.drain(0, Fluids.LAVA, 1, false);
                    setChanged();
                }
                return;
            }

            final var targetTank = blockEntity != null
                    ? Balm.capabilities().getCapability(blockEntity, Direction.UP, CommonCapabilities.FLUID_TANK)
                    : null;
            if (targetTank != null) {
                for (int slot = 0; slot < targetTank.getSlotCount(); slot++) {
                    if (!targetTank.canFill(slot, Fluids.LAVA)) {
                        continue;
                    }
                    final int maxTransfer = lavaTank.getAmount(0);
                    final int accepted = targetTank.fill(slot, Fluids.LAVA, maxTransfer, true);
                    if (accepted > 0) {
                        lavaTank.drain(0, Fluids.LAVA, accepted, false);
                        targetTank.fill(slot, Fluids.LAVA, accepted, false);
                        setChanged();
                    }
                }
                return;
            }

            final var targetState = level.getBlockState(currentPos);
            if (targetState.isFaceSturdy(level, currentPos, Direction.UP) || targetState.isFaceSturdy(level, currentPos, Direction.DOWN)) {
                return;
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        input.child("LavaTank").ifPresent(lavaTank::deserialize);
        processingTicks = input.getIntOr("ProcessingTicks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        lavaTank.serialize(output.child("LavaTank"));
        output.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public Container getContainer(Direction side) {
        return side == Direction.DOWN ? bucketOutputContainer : backingContainer;
    }

    public BalmMenuProvider<Unit> getMenuProvider() {
        return new BalmMenuProvider<>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.lava_sink");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new LavaSinkMenu(containerId, inventory, backingContainer, dataAccess);
            }

            @Override
            public Unit getScreenOpeningData(ServerPlayer player) {
                return Unit.INSTANCE;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        };
    }
}
