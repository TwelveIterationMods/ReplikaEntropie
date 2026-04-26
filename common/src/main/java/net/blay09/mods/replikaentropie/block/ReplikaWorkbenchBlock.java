package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.block.entity.ReplikaWorkbenchBlockEntity;
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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReplikaWorkbenchBlock extends BaseEntityBlock {
    public static final MapCodec<ReplikaWorkbenchBlock> CODEC = simpleCodec(ReplikaWorkbenchBlock::new);

    public static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Shapes.or(
            Shapes.box(0, 0, 0, 1, 5/16f, 1),
            Shapes.box(0.5f/16f, 15/16f, 1/16f, 1 - 0.5f/16f, 1, 1 - 0.5f/16f),
            Shapes.box(0.5f/16f, 5/16f, 1/16f, 2.5/16f, 1, 1 - 0.5f/16f),
            Shapes.box(1 - 2.5f/16f, 5/16f, 1/16f, 1 - 0.5f/16f, 1, 1 - 0.5f/16f),
            Shapes.box(0.5f/16f, 5/16f, 13.5/16f, 1 - 0.5f/16f, 1, 1 - 0.5f/16f)
    ).optimize());

    public ReplikaWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof ReplikaWorkbenchBlockEntity replikaWorkbenchBlockEntity) {
                Balm.networking().openMenu(player, replikaWorkbenchBlockEntity.getMenuProvider());
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReplikaWorkbenchBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.replikaWorkbench.value(), ReplikaWorkbenchBlockEntity::serverTick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
