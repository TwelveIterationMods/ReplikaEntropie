package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.util.FractionalResource;
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
    protected final Container inputContainer;
    protected final Container outputContainer;

    protected final FractionalResource scrap;

    protected static final int INPUT_PROCESSING_TICKS = 50;
    protected static final int OUTPUT_PROCESSING_TICKS = 100;

    protected int inputProcessingTicks;
    protected int outputProcessingTicks;

    protected AbstractScrapGeneratorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
        backingContainer = createBackingContainer();
        outputContainer = new SubContainer(backingContainer, 0, 1);
        inputContainer = new SubContainer(backingContainer, 1, 2);

        scrap = new FractionalResource(outputContainer, 0, ModItems.scrap);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractScrapGeneratorBlockEntity blockEntity) {
        blockEntity.generateInput();
        blockEntity.generateOutput();
    }

    private boolean canGenerateInput() {
        final var inputStack = inputContainer.getItem(0);
        return hasInputResources() && (inputStack.isEmpty() || (isValidInput(inputStack) && inputStack.getCount() < inputStack.getMaxStackSize()));
    }

    protected void generateInput() {
        if (canGenerateInput()) {
            inputProcessingTicks++;
            if (inputProcessingTicks >= INPUT_PROCESSING_TICKS) {
                final var inputStack = inputContainer.getItem(0);
                final var newInputStack = consumeResourcesAndCreateInput();
                if (inputStack.isEmpty()) {
                    inputContainer.setItem(0, newInputStack);
                } else if (inputStack.is(newInputStack.getItem()) && inputStack.getCount() < inputStack.getMaxStackSize()) {
                    inputStack.grow(newInputStack.getCount());
                }
                inputProcessingTicks = 0;
                setChanged();
            }
        } else {
            if (inputProcessingTicks != 0) {
                inputProcessingTicks = 0;
                setChanged();
            }
        }
    }

    private boolean canGenerateOutput() {
        final var inputStack = inputContainer.getItem(0);
        final var outputStack = outputContainer.getItem(0);
        final boolean hasSpaceForScrap = outputStack.isEmpty() || (outputStack.is(ModItems.scrap) && outputStack.getCount() < outputStack.getMaxStackSize());
        return isValidInput(inputStack) && hasSpaceForScrap;
    }

    protected void generateOutput() {
        if (canGenerateOutput()) {
            outputProcessingTicks++;
            if (outputProcessingTicks >= OUTPUT_PROCESSING_TICKS) {
                final var inputStack = inputContainer.getItem(0);
                scrap.add(getScrapPerProcess());
                inputStack.shrink(1);
                outputProcessingTicks = 0;
                setChanged();
            }
        } else {
            if (outputProcessingTicks != 0) {
                outputProcessingTicks = 0;
                setChanged();
            }
        }
    }

    protected abstract DefaultContainer createBackingContainer();
    protected abstract boolean isValidInput(ItemStack itemStack);
    protected abstract boolean hasInputResources();
    protected abstract ItemStack consumeResourcesAndCreateInput();
    protected abstract float getScrapPerProcess();

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        inputProcessingTicks = input.getIntOr("InputProcessingTicks", 0);
        outputProcessingTicks = input.getIntOr("OutputProcessingTicks", 0);
        scrap.setFractionalAmount(input.getFloatOr("FractionalScrap", 0f));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putInt("InputProcessingTicks", inputProcessingTicks);
        output.putInt("OutputProcessingTicks", outputProcessingTicks);
        output.putFloat("FractionalScrap", scrap.getFractionalAmount());
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

}
