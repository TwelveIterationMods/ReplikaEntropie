package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWasteLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class FragmentalWasteBlockEntity extends BlockEntity {
    private final FragmentalWasteLogic logic = new FragmentalWasteLogic(this::setChanged);

    public FragmentalWasteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public FragmentalWasteBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fragmentalWaste.value(), pos, blockState);
    }

    public boolean decontaminationTick() {
        return logic.decontaminationTick(level, Vec3.atCenterOf(worldPosition), worldPosition);
    }

    public void convertToWasteBarrel() {
        if (level instanceof ServerLevel serverLevel) {
            logic.playConversionEffects(serverLevel, Vec3.atBottomCenterOf(worldPosition).add(0, 1.25, 0), worldPosition);
            level.setBlock(worldPosition, ModBlocks.wasteBarrel.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        logic.save(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        logic.load(input);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentalWasteBlockEntity blockEntity) {
        if (level.getGameTime() % 20 == 0) {
            FragmentalWaste.applyWasteAroundBlockEntity(blockEntity);
        }
    }
}
