package net.blay09.mods.replikaentropie.block.entity;

import com.google.common.collect.HashMultiset;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.FragmentAcceleratorMenu;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.blay09.mods.replikaentropie.util.FractionalResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class FragmentAcceleratorBlockEntity extends BalmBlockEntity implements BalmContainerProvider, BalmMenuProvider {

    private static final int PROCESSING_TICKS = 100;
    private static final float SPEED_INCREMENT_PER_COMPLETION = 0.5f;
    private static final float MAX_SPEED_MULTIPLIER = 4f;

    public static final float OUTPUT_MULTIPLIER = 0.25f;
    private static final float MULTIPLIER_BONUS_PER_TYPE = 0.1f;
    private static final float DIMINISHING_RETURNS = 0.5f;

    private final DefaultContainer backingContainer = new DefaultContainer(8) {
        @Override
        public void setChanged() {
            FragmentAcceleratorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack itemStack) {
            return switch (slot) {
                case 0, 1 -> true;
                default -> false;
            };
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch (slot) {
                case 0, 1 -> false;
                default -> true;
            };
        }
    };

    private final Container resultContainer = new SubContainer(backingContainer, 0, 1);
    private final Container wasteContainer = new SubContainer(backingContainer, 1, 2);
    private final Container inputContainer = new SubContainer(backingContainer, 2, 8);
    private final Container outputContainer = new SubContainer(backingContainer, 0, 2);

    private final FractionalResource fragments = new FractionalResource(resultContainer, 0, ModItems.fragments);

    private int processingTicks;
    private float speedMultiplier = 1f;

    private boolean isSyncDirty;
    private int ticksSinceSync;

    private float clientTicksNotProcessing;
    private float clientAngle;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case FragmentAcceleratorMenu.DATA_PROCESSING_TIME -> processingTicks;
                case FragmentAcceleratorMenu.DATA_MAX_PROCESSING_TIME -> getMaxProcessingTicks();
                case FragmentAcceleratorMenu.DATA_FRACTIONAL_FRAGMENTS -> fragments.getFractionalAmountAsMenuData();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return FragmentAcceleratorMenu.DATA_COUNT;
        }
    };

    public FragmentAcceleratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fragmentAccelerator.get(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.fragment_accelerator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FragmentAcceleratorMenu(containerId, inventory, backingContainer, dataAccess);
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentAcceleratorBlockEntity blockEntity) {
        blockEntity.spreadWaste();
        blockEntity.processState();
        blockEntity.broadcastChanges();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, FragmentAcceleratorBlockEntity blockEntity) {
        if (blockEntity.processingTicks <= 0) {
            blockEntity.clientTicksNotProcessing++;
        } else {
            blockEntity.clientTicksNotProcessing = 0;
        }
        final float baseSpeedPerTick = 10f;
        blockEntity.clientAngle = (blockEntity.clientAngle + baseSpeedPerTick * blockEntity.speedMultiplier) % 360f;
    }

    private void spreadWaste() {
        final var wasteStack = wasteContainer.getItem(0);
        if (wasteStack.is(ModBlocks.fragmentalWaste.asItem()) && level.getGameTime() % 20 == 0) {
            FragmentalWaste.applyWasteAroundBlockEntity(this);
        }
    }

    private void processState() {
        if (!canProcess()) {
            processingTicks = 0;
            speedMultiplier = 1f;
            isSyncDirty = true;
            return;
        }

        processingTicks++;
        if (processingTicks >= getMaxProcessingTicks()) {
            processingTicks = 0;
            generateWaste();
            final var results = calculateOutput();
            fragments.add(results.fragments());
            final var maxSpeedMultiplier = Math.min(MAX_SPEED_MULTIPLIER, results.uniqueItems());
            speedMultiplier = Math.min(maxSpeedMultiplier, speedMultiplier + SPEED_INCREMENT_PER_COMPLETION);

            spinInputItems();
            isSyncDirty = true;
        }
    }

    private void spinInputItems() {
        final var participants = new IntArrayList();
        for (int i = 0; i < inputContainer.getContainerSize(); i++) {
            ItemStack stack = inputContainer.getItem(i);
            if (stack.isEmpty() || isValidInput(stack)) {
                participants.add(i);
            }
        }
        if (participants.size() <= 1) {
            return;
        }

        // Take a snapshot we use to reconstruct the rotated grid
        final var snapshot = new ArrayList<ItemStack>(participants.size());
        for (int index : participants) {
            snapshot.add(inputContainer.getItem(index).copy());
        }

        // Apply clockwise rotation by moving each participant to the previous participant's position
        for (int i = 0; i < participants.size(); i++) {
            final var targetIndex = participants.getInt(i);
            final var sourceIndex = (i - 1 + participants.size()) % participants.size();
            final var previous = snapshot.get(sourceIndex);
            inputContainer.setItem(targetIndex, previous);
        }
    }

    private boolean canProcess() {
        return hasAnyValidInput() && hasSpaceForWaste() && fragments.hasSpace();
    }

    private boolean isValidInput(ItemStack itemStack) {
        return !itemStack.isEmpty() && RecyclerRecipe.getRecipe(level, itemStack)
                .map(RecyclerRecipe::fragments)
                .filter(it -> it > 0)
                .isPresent();
    }

    private boolean hasAnyValidInput() {
        for (int i = 0; i < inputContainer.getContainerSize(); i++) {
            final var stack = inputContainer.getItem(i);
            if (isValidInput(stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSpaceForWaste() {
        final var wasteSlotItem = wasteContainer.getItem(0);
        return wasteSlotItem.isEmpty() ||
                (wasteSlotItem.is(ModBlocks.fragmentalWaste.asItem()) &&
                        wasteSlotItem.getCount() < wasteSlotItem.getMaxStackSize());
    }

    private void generateWaste() {
        final var wasteSlotItem = wasteContainer.getItem(0);
        if (wasteSlotItem.isEmpty()) {
            wasteContainer.setItem(0, new ItemStack(ModBlocks.fragmentalWaste, 1));
        } else if (wasteSlotItem.is(ModBlocks.fragmentalWaste.asItem()) &&
                wasteSlotItem.getCount() < wasteSlotItem.getMaxStackSize()) {
            wasteSlotItem.grow(1);
        }
    }

    private int getMaxProcessingTicks() {
        return Math.max(1, (int) Math.ceil(PROCESSING_TICKS / speedMultiplier));
    }

    private record ProcessResults(float fragments, int uniqueItems) {
    }

    private ProcessResults calculateOutput() {
        final var uniqueKinds = HashMultiset.<Item>create();
        var output = 0f;
        for (int i = 0; i < inputContainer.getContainerSize(); i++) {
            final var itemStack = inputContainer.getItem(i);
            final float recipeFragments = RecyclerRecipe.getRecipe(level, itemStack)
                    .map(RecyclerRecipe::fragments)
                    .orElse(0f);
            if (recipeFragments > 0) {
                final var item = itemStack.getItem();
                final var existing = uniqueKinds.count(item);
                output += (float) (recipeFragments * Math.pow(DIMINISHING_RETURNS, existing));
                uniqueKinds.add(item);
            }
        }

        final var itemTypes = uniqueKinds.elementSet().size();
        final var varietyMultiplier = 1f + MULTIPLIER_BONUS_PER_TYPE * (itemTypes - 1);
        final var result = output * OUTPUT_MULTIPLIER * varietyMultiplier;
        return new ProcessResults(result, itemTypes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, backingContainer.getItems());
        processingTicks = tag.getInt("ProcessingTicks");
        fragments.setFractionalAmount(tag.getFloat("FractionalFragments"));
        speedMultiplier = Math.max(1f, tag.getFloat("SpeedMultiplier"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putInt("ProcessingTicks", processingTicks);
        tag.putFloat("FractionalFragments", fragments.getFractionalAmount());
        tag.putFloat("SpeedMultiplier", speedMultiplier);
    }

    @Override
    protected void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putFloat("SpeedMultiplier", speedMultiplier);
        tag.putFloat("ProcessingTicks", processingTicks);
    }

    private void broadcastChanges() {
        ticksSinceSync++;
        final boolean isProcessing = canProcess();
        if (isSyncDirty || (ticksSinceSync >= 10 && isProcessing)) {
            sync();
            isSyncDirty = false;
            ticksSinceSync = 0;
        }
    }

    public Container getInputContainer() {
        return inputContainer;
    }

    public float getClientAngle() {
        return clientAngle;
    }

    public boolean isClientSpinning() {
        return clientTicksNotProcessing < 20;
    }
}
