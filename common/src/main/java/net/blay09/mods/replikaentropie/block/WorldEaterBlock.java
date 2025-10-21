package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.block.entity.WorldEaterBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WorldEaterBlock extends BaseEntityBlock {

    public static final VoxelShape[] SHAPES = new VoxelShape[] {
            Shapes.or(
                    Shapes.box(0, 0, 0, 1, 1, 13 / 16f),
                    Shapes.box(1 / 16f, 1 / 16f, 13 / 16f, 15 / 16f, 15 / 16f, 15 / 16f),
                    Shapes.box(0, 0, 15 / 16f, 1, 1, 1)
            ).optimize(),
            Shapes.or(
                    Shapes.box(3 / 16f, 0, 0, 1, 1, 1),
                    Shapes.box(1 / 16f, 1 / 16f, 1 / 16f, 3 / 16f, 15 / 16f, 15 / 16f),
                    Shapes.box(0, 0, 0, 1 / 16f, 1, 1)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 3 / 16f, 1, 1, 1),
                    Shapes.box(1 / 16f, 1 / 16f, 1 / 16f, 15 / 16f, 15 / 16f, 3 / 16f),
                    Shapes.box(0, 0, 0, 1, 1, 1 / 16f)
            ).optimize(),
            Shapes.or(
                    Shapes.box(0, 0, 0, 13 / 16f, 1, 1),
                    Shapes.box(13 / 16f, 1 / 16f, 1 / 16f, 15 / 16f, 15 / 16f, 15 / 16f),
                    Shapes.box(15 / 16f, 0, 0, 1, 1, 1)
            ).optimize()
    };

    public WorldEaterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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
        return new WorldEaterBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof WorldEaterBlockEntity worldEaterBlockEntity) {
                Balm.getNetworking().openMenu(player, worldEaterBlockEntity);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.worldEater.get(), WorldEaterBlockEntity::serverTick);
    }
}
