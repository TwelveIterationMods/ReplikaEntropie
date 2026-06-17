package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FragmentalSunBlock extends Block {
    public static final MapCodec<FragmentalSunBlock> CODEC = simpleCodec(FragmentalSunBlock::new);

    private static final VoxelShape SHAPE = Shapes.box(4.5 / 16f, 3.5 / 16f, 4.5 / 16f, 11.5 / 16f, 10.5 / 16f, 11.5 / 16f);

    public FragmentalSunBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        //return SHAPE;
        return Shapes.box(4.5 / 16f, 3.5 / 16f, 4.5 / 16f, 11.5 / 16f, 10.5 / 16f, 11.5 / 16f);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() > 0.7f) {
            return;
        }

        final double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.75;
        final double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.75;
        final double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.75;
        final double velocityY = 0.01 + random.nextDouble() * 0.02;
        level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, velocityY, 0);

        if (random.nextFloat() < 0.35f) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
        }
    }
}
