package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.replikaentropie.block.entity.DigSpotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class DigSpotBlock extends BaseEntityBlock {

    public static final MapCodec<DigSpotBlock> CODEC = simpleCodec(DigSpotBlock::new);
    private static final VoxelShape SHAPE = Shapes.empty();

    public DigSpotBlock(Properties properties) {
        super(properties.replaceable());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DigSpotBlockEntity(blockPos, blockState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        final var belowPos = pos.below();
        return Block.isFaceFull(level.getBlockState(belowPos).getCollisionShape(level, belowPos), Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource randomSource) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.3f) {
            final var x = pos.getX() + 0.5f;
            final var y = pos.getY() + 0.05f;
            final var z = pos.getZ() + 0.5f;
            level.addParticle(ParticleTypes.EGG_CRACK,
                    x + (random.nextDouble() - 0.5) * 0.2,
                    y,
                    z + (random.nextDouble() - 0.5) * 0.2,
                    0.0,
                    0.0,
                    0.0);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
