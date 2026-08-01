package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.api.crane.CraneHandlerRegistry;
import net.blay09.mods.replikaentropie.api.crane.CraneTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class CraneBlockEntity extends BlockEntity {
    public static final int MAGNET_VIBRATION_TICKS = 8;
    public static final int BOTH_VIBRATION_TICKS = 8;
    public static final int BLOCK_SNAP_TICKS = 5;
    public static final int SNAP_PAUSE_TICKS = 5;
    public static final int ARM_ROTATION_TICKS = 14;
    public static final int MAGNET_BOUNCE_TICKS = 5;
    public static final int BLOCK_DROP_TICKS = 9;
    public static final int ARM_RETURN_TICKS = 12;
    private static final int BLOCK_PICKUP_START = MAGNET_VIBRATION_TICKS + BOTH_VIBRATION_TICKS;
    private static final int BLOCK_PICKUP_END = MAGNET_VIBRATION_TICKS + BOTH_VIBRATION_TICKS + BLOCK_SNAP_TICKS;
    public static final int TRANSFER_TICKS = MAGNET_VIBRATION_TICKS
            + BOTH_VIBRATION_TICKS
            + BLOCK_SNAP_TICKS
            + SNAP_PAUSE_TICKS
            + ARM_ROTATION_TICKS
            + BLOCK_DROP_TICKS;
    public static final int REDSTONE_PULSE_TICKS = 10;

    private BlockState carriedState = Blocks.AIR.defaultBlockState();
    private @Nullable CompoundTag carriedBlockEntityData;
    private int transferTicks;
    private int armReturnTicks;
    private boolean returningToSource;
    private boolean armReturning;

    public CraneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.crane.value(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CraneBlockEntity crane) {
        if (crane.armReturning) {
            crane.continueArmReturn();
        } else if (crane.carriedState.isAir()) {
            crane.tryStartTransfer();
        } else {
            crane.continueTransfer();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CraneBlockEntity crane) {
        if (!crane.carriedState.isAir() && crane.transferTicks < TRANSFER_TICKS) {
            crane.transferTicks++;
        } else if (crane.armReturning && crane.armReturnTicks < ARM_RETURN_TICKS) {
            crane.armReturnTicks++;
            if (crane.armReturnTicks >= ARM_RETURN_TICKS) {
                crane.armReturning = false;
                crane.armReturnTicks = 0;
            }
        }
    }

    private void tryStartTransfer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final var sourcePos = getSourcePos();
        final var destinationPos = getDestinationPos();
        if (!serverLevel.isLoaded(sourcePos) || !serverLevel.isLoaded(destinationPos)) {
            return;
        }

        final var transfer = CraneHandlerRegistry.tryPickup(serverLevel, sourcePos, destinationPos);
        transfer.ifPresent(craneTransfer -> startTransfer(craneTransfer.state(), craneTransfer.blockEntityData()));
    }

    private void startTransfer(BlockState carriedState, @Nullable CompoundTag carriedBlockEntityData) {
        this.carriedState = carriedState;
        this.carriedBlockEntityData = carriedBlockEntityData;
        transferTicks = 0;
        armReturnTicks = 0;
        returningToSource = false;
        armReturning = false;
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    private void continueTransfer() {
        transferTicks++;
        if (transferTicks == BLOCK_PICKUP_START) {
            level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 0.8f);
        } else if (transferTicks == BLOCK_PICKUP_END) {
            level.playSound(null, worldPosition, carriedState.getSoundType().getFallSound(), SoundSource.BLOCKS, 0.5f, 1f);
        }
        if (transferTicks < TRANSFER_TICKS) {
            return;
        }

        final var targetPos = returningToSource ? getSourcePos() : getDestinationPos();
        if (tryPlaceCarriedBlock(targetPos)) {
            final boolean placedAtDestination = !returningToSource;
            level.playSound(null, worldPosition, carriedState.getSoundType().getFallSound(), SoundSource.BLOCKS, 0.5f, 1f);
            clearCarriedBlock();
            if (placedAtDestination) {
                startArmReturn();
                emitRedstonePulse();
            }
        } else {
            returningToSource = !returningToSource;
            transferTicks = 0;
        }
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    private void continueArmReturn() {
        armReturnTicks++;
        if (armReturnTicks < ARM_RETURN_TICKS) {
            return;
        }

        armReturning = false;
        armReturnTicks = 0;
        setChanged();
    }

    private void startArmReturn() {
        armReturning = true;
        armReturnTicks = 0;
    }

    private void emitRedstonePulse() {
        final var state = getBlockState();
        if (!state.getValue(BlockStateProperties.POWERED)) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_CLIENTS);
            level.updateNeighborsAt(worldPosition, state.getBlock());
        }
        level.scheduleTick(worldPosition, state.getBlock(), REDSTONE_PULSE_TICKS);
    }

    public void releaseCarriedBlock() {
        if (level == null || carriedState.isAir()) {
            return;
        }

        final var sourcePos = getSourcePos();
        final var destinationPos = getDestinationPos();
        if (!tryPlaceCarriedBlock(sourcePos) && !tryPlaceCarriedBlock(destinationPos)) {
            final var carriedBlockEntity = carriedBlockEntityData != null
                    ? BlockEntity.loadStatic(worldPosition, carriedState, carriedBlockEntityData, level.registryAccess())
                    : null;
            Block.dropResources(carriedState, level, worldPosition, carriedBlockEntity);
        }
        clearCarriedBlock();
    }

    private boolean tryPlaceCarriedBlock(BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        final var transfer = new CraneTransfer(carriedState, carriedBlockEntityData);
        return CraneHandlerRegistry.tryPlace(serverLevel, pos, transfer);
    }

    private void clearCarriedBlock() {
        carriedState = Blocks.AIR.defaultBlockState();
        carriedBlockEntityData = null;
        transferTicks = 0;
        returningToSource = false;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        releaseCarriedBlock();
        super.preRemoveSideEffects(pos, state);
    }

    public BlockPos getSourcePos() {
        return worldPosition.relative(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    public BlockPos getDestinationPos() {
        final Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return worldPosition.relative(facing.getClockWise());
    }

    public BlockState getCarriedState() {
        return carriedState;
    }

    public float getAnimationTicks(float partialTick) {
        if (carriedState.isAir()) {
            return 0f;
        }

        return Math.min(TRANSFER_TICKS, transferTicks + partialTick);
    }

    public boolean isReturningToSource() {
        return returningToSource;
    }

    public boolean isArmReturning() {
        return armReturning;
    }

    public float getArmReturnTicks(float partialTick) {
        if (!armReturning) {
            return 0f;
        }

        return Math.min(ARM_RETURN_TICKS, armReturnTicks + partialTick);
    }

    public float getTransferProgress(float partialTick) {
        if (carriedState.isAir()) {
            return 0f;
        }

        final float progress = Math.min(1f, (transferTicks + partialTick) / TRANSFER_TICKS);
        return returningToSource ? 1f - progress : progress;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        carriedState = input.read("CarriedState", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
        carriedBlockEntityData = input.read("CarriedBlockEntity", CompoundTag.CODEC).orElse(null);
        transferTicks = input.getIntOr("TransferTicks", 0);
        armReturnTicks = input.getIntOr("ArmReturnTicks", 0);
        returningToSource = input.getBooleanOr("ReturningToSource", false);
        armReturning = input.getBooleanOr("ArmReturning", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (!carriedState.isAir()) {
            output.store("CarriedState", BlockState.CODEC, carriedState);
            output.storeNullable("CarriedBlockEntity", CompoundTag.CODEC, carriedBlockEntityData);
            output.putInt("TransferTicks", transferTicks);
            output.putBoolean("ReturningToSource", returningToSource);
        }
        if (armReturning) {
            output.putInt("ArmReturnTicks", armReturnTicks);
            output.putBoolean("ArmReturning", true);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, this::writeUpdateTag);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return BalmBlockEntityUtils.createUpdatePacket(this);
    }

    private void writeUpdateTag(ValueOutput output) {
        if (!carriedState.isAir()) {
            output.store("CarriedState", BlockState.CODEC, carriedState);
            output.putInt("TransferTicks", transferTicks);
            output.putBoolean("ReturningToSource", returningToSource);
        }
        if (armReturning) {
            output.putInt("ArmReturnTicks", armReturnTicks);
            output.putBoolean("ArmReturning", true);
        }
    }
}
