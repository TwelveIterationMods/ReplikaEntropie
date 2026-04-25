package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class AbstractScrapGeneratorBlockEntity extends BlockEntity implements BalmContainerProvider {

    protected final DefaultContainer backingContainer;
    protected final Container outputContainer;

    protected static final int PROCESSING_TICKS = 50;

    protected int processingTicks;

    protected AbstractScrapGeneratorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
        backingContainer = createBackingContainer();
        outputContainer = new SubContainer(backingContainer, 0, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractScrapGeneratorBlockEntity blockEntity) {
        blockEntity.generateResource();
    }

    private boolean canGenerate() {
        final var inputStack = outputContainer.getItem(0);
        return hasInputResources() && (inputStack.isEmpty() || (isValidInput(inputStack) && inputStack.getCount() < inputStack.getMaxStackSize()));
    }

    protected void generateResource() {
        if (canGenerate()) {
            processingTicks++;
            if (processingTicks >= PROCESSING_TICKS) {
                final var inputStack = outputContainer.getItem(0);
                final var newInputStack = consumeResourcesAndCreateInput();
                if (inputStack.isEmpty()) {
                    outputContainer.setItem(0, newInputStack);
                } else if (inputStack.is(newInputStack.getItem()) && inputStack.getCount() < inputStack.getMaxStackSize()) {
                    inputStack.grow(newInputStack.getCount());
                }
                processingTicks = 0;
                setChanged();
            }
        } else {
            if (processingTicks != 0) {
                processingTicks = 0;
                setChanged();
            }
        }
    }

    protected abstract DefaultContainer createBackingContainer();
    protected abstract boolean isValidInput(ItemStack itemStack);
    protected abstract boolean hasInputResources();
    protected abstract ItemStack consumeResourcesAndCreateInput();

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        processingTicks = input.getIntOr("ProcessingTicks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

}
