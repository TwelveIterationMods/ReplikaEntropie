package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.replikaentropie.block.entity.CraneBlockEntity;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CraneBlock extends BaseEntityBlock {
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 0, 2, 14, 2, 14),
            box(5, 2, 5, 11, 16, 11)
    ).optimize();
    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(4, 0, 4, 12, 2, 12),
            box(7, 2, 7, 9, 16, 9)
    ).optimize();

    public CraneBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.POWERED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        final var abovePos = context.getClickedPos().above();
        if (!context.getLevel().getBlockState(abovePos).canBeReplaced(context) || !context.getLevel().getWorldBorder().isWithinBounds(abovePos)) {
            return null;
        }

        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection())
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.POWERED, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource randomSource) {
        final var half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        final var directionToOtherHalf = half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN;
        if (direction == directionToOtherHalf && (!neighborState.is(this) || neighborState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == half)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        final var half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if (half == DoubleBlockHalf.UPPER) {
            final var lowerState = level.getBlockState(pos.below());
            return lowerState.is(this) && lowerState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        }

        return super.canSurvive(state, level, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            final var lowerPos = pos.below();
            final var lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this) && lowerState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                level.destroyBlock(lowerPos, !player.isCreative(), player, 512);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
        }

        return 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
        }

        return 0;

    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER
                && state.getValue(BlockStateProperties.POWERED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, false), Block.UPDATE_CLIENTS);
            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? new CraneBlockEntity(pos, state) : null;

    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER) {
            return null;
        }

        return level.isClientSide()
                ? createTickerHelper(type, ModBlockEntities.crane.value(), CraneBlockEntity::clientTick)
                : createTickerHelper(type, ModBlockEntities.crane.value(), CraneBlockEntity::serverTick);
    }

}
