package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FragmentalWasteBlockEntity extends BlockEntity {

    private int wasteCount = 1;

    public FragmentalWasteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public FragmentalWasteBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fragmentalWaste.get(), pos, blockState);
    }

    public int getWasteCount() {
        return wasteCount;
    }

    public void setWasteCount(int count) {
        if (count != this.wasteCount) {
            this.wasteCount = count;
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("WasteCount", wasteCount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("WasteCount")) {
            wasteCount = tag.getInt("WasteCount");
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentalWasteBlockEntity blockEntity) {
        if (level.getGameTime() % 20 == 0) {
            FragmentalWaste.applyWasteAroundBlockEntity(blockEntity);
        }
    }
}
