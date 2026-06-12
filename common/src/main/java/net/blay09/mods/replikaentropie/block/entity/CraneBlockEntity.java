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
    public static final int TRANSFER_TICKS = 40;
    public static final int REDSTONE_PULSE_TICKS = 10;

    private BlockState carriedState = Blocks.AIR.defaultBlockState();
    private @Nullable CompoundTag carriedBlockEntityData;
    private int transferTicks;
    private boolean returningToSource;

    public CraneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.crane.value(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CraneBlockEntity crane) {
        if (crane.carriedState.isAir()) {
            crane.tryStartTransfer();
        } else {
            crane.continueTransfer();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CraneBlockEntity crane) {
        if (!crane.carriedState.isAir() && crane.transferTicks < TRANSFER_TICKS) {
            crane.transferTicks++;
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
        if (transfer.isPresent()) {
            startTransfer(transfer.get().state(), transfer.get().blockEntityData());
        }
    }

    private void startTransfer(BlockState carriedState, @Nullable CompoundTag carriedBlockEntityData) {
        this.carriedState = carriedState;
        this.carriedBlockEntityData = carriedBlockEntityData;
        transferTicks = 0;
        returningToSource = false;
        level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 1f);
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    private void continueTransfer() {
        transferTicks++;
        if (transferTicks < TRANSFER_TICKS) {
            setChanged();
            return;
        }

        final var targetPos = returningToSource ? getSourcePos() : getDestinationPos();
        if (tryPlaceCarriedBlock(targetPos)) {
            final boolean placedAtDestination = !returningToSource;
            level.playSound(null, worldPosition, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5f, 1f);
            clearCarriedBlock();
            if (placedAtDestination) {
                emitRedstonePulse();
            }
        } else {
            returningToSource = !returningToSource;
            transferTicks = 0;
            level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 1f);
        }
        setChanged();
        BalmBlockEntityUtils.sync(this);
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
        returningToSource = input.getBooleanOr("ReturningToSource", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (!carriedState.isAir()) {
            output.store("CarriedState", BlockState.CODEC, carriedState);
            output.storeNullable("CarriedBlockEntity", CompoundTag.CODEC, carriedBlockEntityData);
            output.putInt("TransferTicks", transferTicks);
            output.putBoolean("ReturningToSource", returningToSource);
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
    }
}
