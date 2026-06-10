package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
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

    private BlockState carriedState = Blocks.AIR.defaultBlockState();
    private @Nullable CompoundTag carriedBlockEntityData;
    private int transferTicks;

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
        if (!serverLevel.isLoaded(sourcePos) || !serverLevel.isLoaded(destinationPos) || !serverLevel.getBlockState(destinationPos).isAir()) {
            return;
        }

        final var sourceState = serverLevel.getBlockState(sourcePos);
        if (!canMove(serverLevel, sourcePos, sourceState)) {
            return;
        }

        carriedState = sourceState;
        final var sourceBlockEntity = serverLevel.getBlockEntity(sourcePos);
        carriedBlockEntityData = sourceBlockEntity != null ? sourceBlockEntity.saveWithFullMetadata(serverLevel.registryAccess()) : null;
        transferTicks = 0;
        serverLevel.removeBlockEntity(sourcePos);
        serverLevel.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        serverLevel.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 1f);
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    private static boolean canMove(ServerLevel level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && state.getFluidState().isEmpty()
                && state.getDestroySpeed(level, pos) >= 0f
                && !state.is(ModBlockTags.CRANE_RELOCATION_NOT_SUPPORTED);
    }

    private void continueTransfer() {
        transferTicks++;
        if (transferTicks < TRANSFER_TICKS) {
            setChanged();
            return;
        }

        final var destinationPos = getDestinationPos();
        if (tryPlaceCarriedBlock(destinationPos)) {
            level.playSound(null, worldPosition, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5f, 1f);
            clearCarriedBlock();
            setChanged();
            BalmBlockEntityUtils.sync(this);
        }
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
        if (level == null || !level.getBlockState(pos).isAir()) {
            return false;
        }

        if (!level.setBlock(pos, carriedState, Block.UPDATE_ALL)) {
            return false;
        }
        if (carriedBlockEntityData != null) {
            final var blockEntity = BlockEntity.loadStatic(pos, level.getBlockState(pos), carriedBlockEntityData, level.registryAccess());
            if (blockEntity != null) {
                level.setBlockEntity(blockEntity);
                blockEntity.setChanged();
            }
        }
        return true;
    }

    private void clearCarriedBlock() {
        carriedState = Blocks.AIR.defaultBlockState();
        carriedBlockEntityData = null;
        transferTicks = 0;
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
        return carriedState.isAir() ? 0f : Math.min(1f, (transferTicks + partialTick) / TRANSFER_TICKS);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        carriedState = input.read("CarriedState", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
        carriedBlockEntityData = input.read("CarriedBlockEntity", CompoundTag.CODEC).orElse(null);
        transferTicks = input.getIntOr("TransferTicks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (!carriedState.isAir()) {
            output.store("CarriedState", BlockState.CODEC, carriedState);
            output.storeNullable("CarriedBlockEntity", CompoundTag.CODEC, carriedBlockEntityData);
            output.putInt("TransferTicks", transferTicks);
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
        }
    }
}
