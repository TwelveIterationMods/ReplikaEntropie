package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FragmentalWasteBlockEntity extends BlockEntity {
    public static final int DECONTAMINATION_TICKS_REQUIRED = 200;

    private int decontaminationTicks;

    public FragmentalWasteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public FragmentalWasteBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fragmentalWaste.value(), pos, blockState);
    }

    public int getDecontaminationTicks() {
        return decontaminationTicks;
    }

    public boolean addDecontaminationTick() {
        if (decontaminationTicks < DECONTAMINATION_TICKS_REQUIRED) {
            decontaminationTicks++;
            if(level instanceof ServerLevel serverLevel) {
                if (decontaminationTicks % 20 == 0) {
                    serverLevel.sendParticles(ParticleTypes.DUST_PLUME, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 4, 0.75f, 0.75f, 0.75f, 0);
                }
                if (decontaminationTicks % 40 == 0) {
                    serverLevel.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1f, (float) (0.5 + Math.random()));
                }
            }
            setChanged();
        }
        return decontaminationTicks >= DECONTAMINATION_TICKS_REQUIRED;
    }

    public void convertToWasteBarrel() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, worldPosition.getX() + 0.5, worldPosition.getY() + 1.25, worldPosition.getZ() + 0.5, 4, 0.25f, 0.25f, 0.25f, 0);
            serverLevel.playSound(null, worldPosition, SoundEvents.ALLAY_DEATH, SoundSource.BLOCKS, 1f, (float) (0.5 + Math.random()));
            level.setBlock(worldPosition, ModBlocks.wasteBarrel.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("DecontaminationTicks", decontaminationTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        decontaminationTicks = input.getIntOr("DecontaminationTicks", 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentalWasteBlockEntity blockEntity) {
        if (level.getGameTime() % 20 == 0) {
            FragmentalWaste.applyWasteAroundBlockEntity(blockEntity);
        }
    }
}
