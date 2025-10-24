package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.replikaentropie.block.entity.ChaosEngineBlockEntity;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChaosEngineBlock extends BaseEntityBlock {

    public static final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(0, 2 / 16f, 14 / 16f, 7 / 16f, 9 / 16f, 1f),
                    Shapes.box(0, 9 / 16f, 0, 1, 11 / 16f, 2 / 16f),
                    Shapes.box(0, 9 / 16f, 14 / 16f, 1, 11 / 16f, 1f),
                    Shapes.box(14 / 16f, 9 / 16f, 2 / 16f, 1, 11 / 16f, 14 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(0, 2 / 16f, 0, 2 / 16f, 9 / 16f, 7 / 16f),
                    Shapes.box(14 / 16f, 9 / 16f, 0, 1, 11 / 16f, 1),
                    Shapes.box(0, 9 / 16f, 0, 2 / 16f, 11 / 16f, 1),
                    Shapes.box(2 / 16f, 9 / 16f, 14 / 16f, 14 / 16f, 11 / 16f, 1)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(9 / 16f, 2 / 16f, 0, 1f, 9 / 16f, 2 / 16f),
                    Shapes.box(0, 9 / 16f, 14 / 16f, 1, 11 / 16f, 1f),
                    Shapes.box(0, 9 / 16f, 0, 1, 11 / 16f, 2 / 16f),
                    Shapes.box(0, 9 / 16f, 2 / 16f, 2 / 16f, 11 / 16f, 14 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(14 / 16f, 2 / 16f, 9 / 16f, 1f, 9 / 16f, 1f),
                    Shapes.box(0, 9 / 16f, 0, 2 / 16f, 11 / 16f, 1),
                    Shapes.box(14 / 16f, 9 / 16f, 0, 1, 11 / 16f, 1),
                    Shapes.box(2 / 16f, 9 / 16f, 0, 14 / 16f, 11 / 16f, 2 / 16f)
            ).optimize()
    };

    public static final VoxelShape[] COLLISION_SHAPES = new VoxelShape[]{
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(0, 2 / 16f, 14 / 16f, 7 / 16f, 9 / 16f, 1f),
                    Shapes.box(0, 9 / 16f, 0, 1, 12 / 16f, 2 / 16f),
                    Shapes.box(0, 9 / 16f, 14 / 16f, 1, 12 / 16f, 1f),
                    Shapes.box(14 / 16f, 9 / 16f, 2 / 16f, 1, 12 / 16f, 14 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(0, 2 / 16f, 0, 2 / 16f, 9 / 16f, 7 / 16f),
                    Shapes.box(14 / 16f, 9 / 16f, 0, 1, 12 / 16f, 1),
                    Shapes.box(0, 9 / 16f, 0, 2 / 16f, 12 / 16f, 1),
                    Shapes.box(2 / 16f, 9 / 16f, 14 / 16f, 14 / 16f, 12 / 16f, 1)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(9 / 16f, 2 / 16f, 0, 1f, 9 / 16f, 2 / 16f),
                    Shapes.box(0, 9 / 16f, 14 / 16f, 1, 12 / 16f, 1f),
                    Shapes.box(0, 9 / 16f, 0, 1, 12 / 16f, 2 / 16f),
                    Shapes.box(0, 9 / 16f, 2 / 16f, 2 / 16f, 12 / 16f, 14 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 2 / 16f, 1),
                    Shapes.box(14 / 16f, 2 / 16f, 9 / 16f, 1f, 9 / 16f, 1f),
                    Shapes.box(0, 9 / 16f, 0, 2 / 16f, 12 / 16f, 1),
                    Shapes.box(14 / 16f, 9 / 16f, 0, 1, 12 / 16f, 1),
                    Shapes.box(2 / 16f, 9 / 16f, 0, 14 / 16f, 12 / 16f, 2 / 16f)
            ).optimize()
    };

    public ChaosEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue()];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES[state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue()];
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChaosEngineBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? createTickerHelper(type, ModBlockEntities.chaosEngine.get(), ChaosEngineBlockEntity::clientTick)
                : createTickerHelper(type, ModBlockEntities.chaosEngine.get(), ChaosEngineBlockEntity::serverTick);
    }

}
