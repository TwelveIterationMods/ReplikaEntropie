package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.fluid.FluidTank;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.menu.LavascrapMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.material.Fluids;

public class LavascrapBlockEntity extends AbstractScrapGeneratorBlockEntity {

    private final Container waterInputContainer = new SubContainer(backingContainer, 2, 3);
    private final Container lavaInputContainer = new SubContainer(backingContainer, 3, 4);

    private final FluidTank waterTank = new FluidTank(3000);
    private final FluidTank lavaTank = new FluidTank(3000);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case LavascrapMenu.DATA_INPUT_GENERATION_TIME -> inputProcessingTicks;
                case LavascrapMenu.DATA_MAX_INPUT_GENERATION_TIME -> INPUT_PROCESSING_TICKS;
                case LavascrapMenu.DATA_OUTPUT_GENERATION_TIME -> outputProcessingTicks;
                case LavascrapMenu.DATA_MAX_OUTPUT_GENERATION_TIME -> OUTPUT_PROCESSING_TICKS;
                case LavascrapMenu.DATA_SCRAP_FRACTIONAL -> scrap.getFractionalAmountAsMenuData();
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
        super(ModBlockEntities.lavascrap.get(), blockPos, blockState);
    }

    @Override
    protected DefaultContainer createBackingContainer() {
        return new DefaultContainer(4) {
            @Override
            public void setChanged() {
                LavascrapBlockEntity.this.setChanged();
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
                return switch(index) {
                    case 0 -> false;
                    case 1 -> isValidInput(stack);
                    case 2 -> stack.is(Items.WATER_BUCKET);
                    case 3 -> stack.is(Items.LAVA_BUCKET);
                    default -> true;
                };
            }

            @Override
            public boolean canTakeItem(Container target, int index, ItemStack stack) {
                return switch(index) {
                    case 0 -> true;
                    case 2, 3 -> stack.is(Items.BUCKET);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        waterTank.deserialize(tag.getCompound("WaterTank"));
        lavaTank.deserialize(tag.getCompound("LavaTank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("WaterTank", waterTank.serialize());
        tag.put("LavaTank", lavaTank.serialize());
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.lavascrap");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new LavascrapMenu(containerId, inventory, backingContainer, dataAccess);
            }
        };
    }

    @Override
    protected float getScrapPerProcess() {
        return 1.5f;
    }
}
