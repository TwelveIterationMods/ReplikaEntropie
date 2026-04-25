package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.fluid.DefaultFluidTank;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.replikaentropie.menu.LavascrapMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LavascrapBlockEntity extends AbstractScrapGeneratorBlockEntity {

    public static final int CONTAINER_SIZE = 3;

    private final Container waterInputContainer = new SubContainer(backingContainer, 1, 2);
    private final Container lavaInputContainer = new SubContainer(backingContainer, 2, 3);

    private final DefaultFluidTank waterTank = new DefaultFluidTank(3000);
    private final DefaultFluidTank lavaTank = new DefaultFluidTank(3000);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case LavascrapMenu.DATA_PROCESSING_TICKS -> processingTicks;
                case LavascrapMenu.DATA_MAX_PROCESSING_TICKS -> PROCESSING_TICKS;
                case LavascrapMenu.DATA_WATER_TANK -> waterTank.getAmount();
                case LavascrapMenu.DATA_MAX_WATER_TANK -> waterTank.getCapacity();
                case LavascrapMenu.DATA_LAVA_TANK -> lavaTank.getAmount();
                case LavascrapMenu.DATA_MAX_LAVA_TANK -> lavaTank.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return LavascrapMenu.DATA_COUNT;
        }
    };

    public LavascrapBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.lavascrap.value(), blockPos, blockState);
    }

    @Override
    protected DefaultContainer createBackingContainer() {
        return new DefaultContainer(CONTAINER_SIZE) {
            @Override
            public void setChanged() {
                LavascrapBlockEntity.this.setChanged();
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
                return switch(index) {
                    case 0 -> false;
                    case 1 -> stack.is(Items.WATER_BUCKET);
                    case 2 -> stack.is(Items.LAVA_BUCKET);
                    default -> true;
                };
            }

            @Override
            public boolean canTakeItem(Container target, int index, ItemStack stack) {
                return switch(index) {
                    case 0 -> true;
                    case 1, 2 -> stack.is(Items.BUCKET);
                    default -> false;
                };
            }
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LavascrapBlockEntity blockEntity) {
        blockEntity.processBuckets();
        AbstractScrapGeneratorBlockEntity.serverTick(level, pos, state, blockEntity);
    }

    private void processBuckets() {
        final var waterBucket = waterInputContainer.getItem(0);
        if (waterBucket.is(Items.WATER_BUCKET) && waterTank.getAmount() + 1000 <= waterTank.getCapacity()) {
            waterTank.fill(Fluids.WATER, 1000, false);
            waterInputContainer.setItem(0, new ItemStack(Items.BUCKET));
            setChanged();
        }

        final var lavaBucket = lavaInputContainer.getItem(0);
        if (lavaBucket.is(Items.LAVA_BUCKET) && lavaTank.getAmount() + 1000 <= lavaTank.getCapacity()) {
            lavaTank.fill(Fluids.LAVA, 1000, false);
            lavaInputContainer.setItem(0, new ItemStack(Items.BUCKET));
            setChanged();
        }
    }

    @Override
    protected boolean isValidInput(ItemStack itemStack) {
        return itemStack.is(Items.OBSIDIAN);
    }

    @Override
    protected boolean hasInputResources() {
        return waterTank.getAmount() >= 1000 && lavaTank.getAmount() >= 1000;
    }

    @Override
    protected ItemStack consumeResourcesAndCreateInput() {
        waterTank.drain(Fluids.WATER, 1000, false);
        lavaTank.drain(Fluids.LAVA, 1000, false);
        return new ItemStack(Items.OBSIDIAN);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        input.child("WaterTank").ifPresent(waterTank::deserialize);
        input.child("LavaTank").ifPresent(lavaTank::deserialize);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        waterTank.serialize(output.child("WaterTank"));
        lavaTank.serialize(output.child("LavaTank"));
    }

    public BalmMenuProvider<Unit> getMenuProvider() {
        return new BalmMenuProvider<>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.lavascrap");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new LavascrapMenu(containerId, inventory, backingContainer, dataAccess);
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
