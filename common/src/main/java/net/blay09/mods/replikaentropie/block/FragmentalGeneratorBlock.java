package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.block.entity.FragmentalGeneratorBlockEntity;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FragmentalGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<FragmentalGeneratorBlock> CODEC = simpleCodec(FragmentalGeneratorBlock::new);

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0, 0, 1, 1/16f, 1),
            Shapes.box(1/16f, 1/16f, 1/16f, 15/16f, 15/16f, 15/16f),
            Shapes.box(0, 15/16f, 0, 1, 1f, 1)
    ).optimize();

    public FragmentalGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FragmentalGeneratorBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof FragmentalGeneratorBlockEntity fragmentalGeneratorBlockEntity) {
                Balm.networking().openMenu(player, fragmentalGeneratorBlockEntity);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof FragmentalGeneratorBlockEntity blockEntity) {
            return blockEntity.getComparatorOutput();
        }

        return 0;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide()
                ? createTickerHelper(type, ModBlockEntities.fragmentalGenerator.value(), FragmentalGeneratorBlockEntity::clientTick)
                : createTickerHelper(type, ModBlockEntities.fragmentalGenerator.value(), FragmentalGeneratorBlockEntity::serverTick);
    }
}
