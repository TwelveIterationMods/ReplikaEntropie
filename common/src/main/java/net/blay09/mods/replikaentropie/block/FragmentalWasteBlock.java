package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.replikaentropie.block.entity.FragmentalWasteBlockEntity;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FragmentalWasteBlock extends BaseEntityBlock {

    private final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 2, 12, 16, 14),
            box(2, 0, 4, 14, 16, 12),
            box(3, 0, 3, 13, 16, 13)
    ).optimize();

    protected FragmentalWasteBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FragmentalWasteBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.fragmentalWaste.get(), FragmentalWasteBlockEntity::serverTick);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        final var drops = super.getDrops(state, params);
        final var result = new ArrayList<>(drops);
        final var blockEntity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof FragmentalWasteBlockEntity fragmentalWaste) {
            for (int i = 1; i < fragmentalWaste.getWasteCount(); i++) {
                for (final var drop : drops) {
                    if (drop.is(ModItems.fragmentalWaste)) {
                        result.add(drop.copy());
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);

        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3f, true, Level.ExplosionInteraction.BLOCK);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        super.onProjectileHit(level, state, hit, projectile);

        final var pos = hit.getBlockPos();
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3f, true, Level.ExplosionInteraction.BLOCK);
    }

}
