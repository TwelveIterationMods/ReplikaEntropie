package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.menu.CobblescrapMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CobblescrapBlockEntity extends AbstractScrapGeneratorBlockEntity {

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case CobblescrapMenu.DATA_INPUT_GENERATION_TIME -> inputProcessingTicks;
                case CobblescrapMenu.DATA_MAX_INPUT_GENERATION_TIME -> INPUT_PROCESSING_TICKS;
                case CobblescrapMenu.DATA_OUTPUT_GENERATION_TIME -> outputProcessingTicks;
                case CobblescrapMenu.DATA_MAX_OUTPUT_GENERATION_TIME -> OUTPUT_PROCESSING_TICKS;
                case CobblescrapMenu.DATA_SCRAP_FRACTIONAL -> scrap.getFractionalAmountAsMenuData();
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
        super(ModBlockEntities.cobblescrap.get(), blockPos, blockState);
    }

    @Override
    protected DefaultContainer createBackingContainer() {
        return new DefaultContainer(2) {
            @Override
            public void setChanged() {
                CobblescrapBlockEntity.this.setChanged();
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
                return switch(index) {
                    case 0 -> false;
                    case 1 -> isValidInput(stack);
                    default -> true;
                };
            }

            @Override
            public boolean canTakeItem(Container target, int index, ItemStack stack) {
                //noinspection SwitchStatementWithTooFewBranches
                return switch(index) {
                    case 0 -> true;
                    default -> false;
                };
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

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.cobblescrap");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new CobblescrapMenu(containerId, inventory, backingContainer, dataAccess);
            }
        };
    }

    @Override
    protected float getScrapPerProcess() {
        return 0.15f;
    }
}
