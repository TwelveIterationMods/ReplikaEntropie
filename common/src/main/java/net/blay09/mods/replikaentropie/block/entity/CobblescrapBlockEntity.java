package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.replikaentropie.menu.CobblescrapMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CobblescrapBlockEntity extends AbstractScrapGeneratorBlockEntity {

    public static final int CONTAINER_SIZE = 1;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case CobblescrapMenu.DATA_PROCESSING_TICKS -> processingTicks;
                case CobblescrapMenu.DATA_MAX_PROCESSING_TICKS -> PROCESSING_TICKS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return CobblescrapMenu.DATA_COUNT;
        }
    };

    public CobblescrapBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.cobblescrap.value(), blockPos, blockState);
    }

    @Override
    protected DefaultContainer createBackingContainer() {
        return new DefaultContainer(CONTAINER_SIZE) {
            @Override
            public void setChanged() {
                CobblescrapBlockEntity.this.setChanged();
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
                return false;
            }
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CobblescrapBlockEntity blockEntity) {
        AbstractScrapGeneratorBlockEntity.serverTick(level, pos, state, blockEntity);
    }

    @Override
    protected boolean isValidInput(ItemStack itemStack) {
        return itemStack.is(Items.COBBLESTONE);
    }

    @Override
    protected boolean hasInputResources() {
        return true;
    }

    @Override
    protected ItemStack consumeResourcesAndCreateInput() {
        return new ItemStack(Items.COBBLESTONE);
    }

    public BalmMenuProvider<Unit> getMenuProvider() {
        return new BalmMenuProvider<>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.cobblescrap");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new CobblescrapMenu(containerId, inventory, backingContainer, dataAccess);
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
