package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.block.entity.EntropicDataMinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EntropicDataMinerBlock extends BaseEntityBlock {

    public static final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.or(
                    Shapes.box(0f, 0f, 0f, 1f, 2 / 16f, 1f),
                    Shapes.box(7 / 16f, 2 / 16f, 0f, 1f, 1f, 1f),
                    Shapes.box(1 / 16f, 2 / 16f, 10 / 16f, 5 / 16f, 11 / 16f, 14 / 16f),
                    Shapes.box(1 / 16f, 2 / 16f, 2 / 16f, 5 / 16f, 14 / 16f, 6 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0f, 0f, 0f, 1f, 2 / 16f, 1f),
                    Shapes.box(0f, 2 / 16f, 7 / 16f, 1f, 1f, 1f),
                    Shapes.box(2 / 16f, 2 / 16f, 1 / 16f, 6 / 16f, 11 / 16f, 5 / 16f),
                    Shapes.box(10 / 16f, 2 / 16f, 1 / 16f, 14 / 16f, 14 / 16f, 5 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0f, 0f, 0f, 1f, 2 / 16f, 1f),
                    Shapes.box(0f, 2 / 16f, 0f, 9 / 16f, 1f, 1f),
                    Shapes.box(11 / 16f, 2 / 16f, 2 / 16f, 15 / 16f, 11 / 16f, 6 / 16f),
                    Shapes.box(11 / 16f, 2 / 16f, 10 / 16f, 15 / 16f, 14 / 16f, 14 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0f, 0f, 0f, 1f, 2 / 16f, 1f),
                    Shapes.box(0f, 2 / 16f, 0f, 1f, 1f, 9 / 16f),
                    Shapes.box(10 / 16f, 2 / 16f, 11 / 16f, 14 / 16f, 11 / 16f, 15 / 16f),
                    Shapes.box(2 / 16f, 2 / 16f, 11 / 16f, 6 / 16f, 14 / 16f, 15 / 16f)
            ).optimize()
    };

    protected EntropicDataMinerBlock(Properties properties) {
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
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EntropicDataMinerBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof EntropicDataMinerBlockEntity blockEntity) {
                Balm.getNetworking().openMenu(player, blockEntity);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
