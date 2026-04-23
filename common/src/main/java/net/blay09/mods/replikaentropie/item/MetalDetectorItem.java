package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.DigSpotBlockEntity;
import net.blay09.mods.replikaentropie.recipe.MetalDetectorRecipe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class MetalDetectorItem extends Item {

    public MetalDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final var level = context.getLevel();
        final var player = context.getPlayer();
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer)) {
            return InteractionResult.SUCCESS;
        }

        final var recipe = MetalDetectorRecipe.getRecipe(level, level.getBlockState(context.getClickedPos()));
        if (recipe.isEmpty()) {
            return InteractionResult.SUCCESS;
        }

        final var digSpotPos = context.getClickedPos().relative(context.getClickedFace());
        if (!level.getBlockState(digSpotPos).canBeReplaced()) {
            return InteractionResult.SUCCESS;
        }

        final var digSpotState = ModBlocks.digSpot.defaultBlockState();
        if (!digSpotState.canSurvive(level, digSpotPos)) {
            return InteractionResult.SUCCESS;
        }

        level.setBlockAndUpdate(digSpotPos, digSpotState);
        if (level.getBlockEntity(digSpotPos) instanceof DigSpotBlockEntity digSpotBlockEntity) {
            digSpotBlockEntity.setLootTable(recipe.get().lootTable());
            digSpotBlockEntity.setChanged();
        }

        serverLevel.playSound(null, digSpotPos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.6f, 1.4f);
        serverLevel.sendParticles(ParticleTypes.CRIT,
                digSpotPos.getX() + 0.5,
                digSpotPos.getY() + 0.1,
                digSpotPos.getZ() + 0.5,
                8,
                0.2,
                0.02,
                0.2,
                0.0);
        return InteractionResult.SUCCESS;
    }
}
