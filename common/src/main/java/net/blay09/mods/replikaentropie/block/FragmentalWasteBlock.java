package net.blay09.mods.replikaentropie.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.replikaentropie.block.entity.FragmentalWasteBlockEntity;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FragmentalWasteBlock extends WasteBarrelBlock {
    public static final MapCodec<FragmentalWasteBlock> CODEC = simpleCodec(FragmentalWasteBlock::new);

    protected FragmentalWasteBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FragmentalWasteBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.fragmentalWaste.value(), FragmentalWasteBlockEntity::serverTick);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        final var drops = super.getDrops(state, params);
        final var result = new ArrayList<>(drops);
        final var blockEntity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof FragmentalWasteBlockEntity fragmentalWaste) {
            for (int i = 1; i < fragmentalWaste.getWasteCount(); i++) {
                for (final var drop : drops) {
                    if (drop.is(ModBlocks.fragmentalWaste.asItem())) {
                        result.add(drop.copy());
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
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
