package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.block.entity.BluePrinterBlockEntity;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class BluePrinterBlock extends BaseEntityBlock {
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Shapes.or(
            Shapes.box(2 / 16f, 2 / 16f, 2 / 16f, 4 / 16f, 14 / 16f, 14 / 16f),
            Shapes.box(4 / 16f, 2 / 16f, 12 / 16f, 12 / 16f, 14 / 16f, 14 / 16f),
            Shapes.box(4 / 16f, 2 / 16f, 2.5 / 16f, 12 / 16f, 14 / 16f, 4.5 / 16f),
            Shapes.box(2 / 16f, 14 / 16f, 2 / 16f, 14 / 16f, 1, 14 / 16f),
            Shapes.box(2 / 16f, 0, 2 / 16f, 14 / 16f, 2 / 16f, 14 / 16f),
            Shapes.box(7 / 16f, 2 / 16f, 7 / 16f, 9 / 16f, 4 / 16f, 9 / 16f),
            Shapes.box(7.5 / 16f, 11 / 16f, 8 / 16f, 8.5 / 16f, 14 / 16f, 9 / 16f),
            Shapes.box(5 / 16f, 4 / 16f, 5 / 16f, 11 / 16f, 5 / 16f, 11 / 16f),
            Shapes.box(12 / 16f, 2 / 16f, 2 / 16f, 14 / 16f, 14 / 16f, 14 / 16f)
    ).optimize());

    protected BluePrinterBlock(Properties properties) {
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
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BluePrinterBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.bluePrinter.value(), BluePrinterBlockEntity::serverTick);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof BluePrinterBlockEntity bluePrinterBlockEntity) {
                Balm.networking().openMenu(player, bluePrinterBlockEntity);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
