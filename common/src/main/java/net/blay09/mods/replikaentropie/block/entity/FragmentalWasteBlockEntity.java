package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FragmentalWasteBlockEntity extends BlockEntity {

    private int wasteCount = 1;

    public FragmentalWasteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public FragmentalWasteBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.fragmentalWaste.value(), pos, blockState);
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
    protected void saveAdditional(ValueOutput output) {
        output.putInt("WasteCount", wasteCount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        wasteCount = input.getIntOr("WasteCount", 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FragmentalWasteBlockEntity blockEntity) {
        if (level.getGameTime() % 20 == 0) {
            FragmentalWaste.applyWasteAroundBlockEntity(blockEntity);
        }
    }
}
