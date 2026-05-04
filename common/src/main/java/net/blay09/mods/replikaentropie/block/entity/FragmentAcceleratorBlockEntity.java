package net.blay09.mods.replikaentropie.block.entity;

import com.google.common.collect.HashMultiset;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.menu.FragmentAcceleratorMenu;
import net.blay09.mods.replikaentropie.recipe.FragmentAcceleratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class FragmentAcceleratorBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit> {

    private static final int PROCESSING_TICKS = 100;
    private static final float SPEED_INCREMENT_PER_COMPLETION = 0.5f;
    private static final float MAX_SPEED_MULTIPLIER = 4f;

    public static final float OUTPUT_MULTIPLIER = 0.25f;
    private static final float MULTIPLIER_BONUS_PER_TYPE = 0.1f;
    private static final float DIMINISHING_RETURNS = 0.5f;
    private static final float WASTE_CHANCE = 0.1f;
    private static final float IDLE_SPEED_FALLOFF = 0.1f;

    private final DefaultContainer backingContainer = new DefaultContainer(8) {
        @Override
        public void setChanged() {
            FragmentAcceleratorBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack itemStack) {
            return switch (slot) {
                case 0 -> true;
                case 1 -> itemStack.is(ModBlocks.fragmentalWaste.asItem());
                default -> false;
            };
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch (slot) {
                case 0 -> false;
                case 1 -> itemStack.is(ModBlocks.wasteBarrel.asItem()) && getItem(1).isEmpty();
                default -> !itemStack.is(ModBlocks.wasteBarrel.asItem());
            };
        }
    };

    private final Container wasteContainer = new SubContainer(backingContainer, 1, 2);
    private final Container inputContainer = new SubContainer(backingContainer, 2, 8);

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
        super(ModBlockEntities.fragmentAccelerator.value(), pos, blockState);
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
    public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
        return Unit.STREAM_CODEC.cast();
    }

    @Override
    public Unit getScreenOpeningData(ServerPlayer player) {
        return Unit.INSTANCE;
    }

    @Override
    public Container getContainer() {
        return backingContainer;
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
        if ((canProcess() || wasteContainer.getItem(0).is(ModBlocks.fragmentalWaste.asItem())) && level.getGameTime() % 20 == 0) {
            FragmentalWaste.applyWasteAroundBlockEntity(this);
        }
    }

    private void processState() {
        if (!canProcess()) {
            processingTicks = 0;
            speedMultiplier = Math.max(1, speedMultiplier - IDLE_SPEED_FALLOFF);
            isSyncDirty = true;
            return;
        }

        processingTicks++;
        if (processingTicks >= getMaxProcessingTicks()) {
            processingTicks = 0;
            if (level.getRandom().nextFloat() <= WASTE_CHANCE) {
                generateWaste();
            }
            final var results = calculateOutput();
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
        return hasAnyValidInput() && hasWasteBarrel();
    }

    private boolean isValidInput(ItemStack itemStack) {
        return !itemStack.isEmpty() && FragmentAcceleratorRecipe.getRecipe(level, itemStack).isPresent();
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

    private boolean hasWasteBarrel() {
        final var wasteSlotItem = wasteContainer.getItem(0);
        return wasteSlotItem.is(ModBlocks.wasteBarrel.asItem());
    }

    private void generateWaste() {
        final var wasteSlotItem = wasteContainer.getItem(0);
        if (wasteSlotItem.is(ModBlocks.wasteBarrel.asItem())) {
            // We re-use the wasteSlotItem count, just to not accidentally void waste barrels that made it past the slot size limit
            wasteContainer.setItem(0, ModBlocks.fragmentalWaste.createStack(wasteSlotItem.getCount()));
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
            final float recipeFragments = FragmentAcceleratorRecipe.getRecipe(level, itemStack)
                    .map(FragmentAcceleratorRecipe::fragments)
                    .orElse(0f);
            if (recipeFragments > 0f) {
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
    protected void loadAdditional(ValueInput input) {
        backingContainer.clearContent();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        processingTicks = input.getIntOr("ProcessingTicks", 0);
        speedMultiplier = Math.max(1f, input.getFloatOr("SpeedMultiplier", 0f));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putInt("ProcessingTicks", processingTicks);
        output.putFloat("SpeedMultiplier", speedMultiplier);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, this::saveAdditional);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return BalmBlockEntityUtils.createUpdatePacket(this);
    }

    private void broadcastChanges() {
        ticksSinceSync++;
        final boolean isProcessing = canProcess();
        if (isSyncDirty || (ticksSinceSync >= 10 && isProcessing)) {
            BalmBlockEntityUtils.sync(this);
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
