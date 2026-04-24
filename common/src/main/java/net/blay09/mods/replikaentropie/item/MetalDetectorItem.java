package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.DigSpotBlockEntity;
import net.blay09.mods.replikaentropie.recipe.MetalDetectorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class MetalDetectorItem extends Item {

    public MetalDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        final var blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        itemStack.hurtAndBreak(1, player, player.getUsedItemHand());
        if (player.tickCount % 8 == 0) {
            player.playSound(SoundEvents.NOTE_BLOCK_COW_BELL.value());
        }
        if (tryCreateDigSpot(level, player, blockHitResult.getBlockPos(), blockHitResult.getDirection())) {
            livingEntity.stopUsingItem();
        }
    }

    private boolean tryCreateDigSpot(Level level, Player player, BlockPos clickedPos, Direction clickedFace) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer)) {
            return false;
        }

        final var recipe = MetalDetectorRecipe.getRecipe(level, level.getBlockState(clickedPos));
        if (recipe.isEmpty()) {
            return false;
        }

        if (level.getRandom().nextFloat() > recipe.get().chance()) {
            return false;
        }

        final var digSpotPos = clickedPos.relative(clickedFace);
        if (!level.getBlockState(digSpotPos).canBeReplaced()) {
            return false;
        }

        final var digSpotState = ModBlocks.digSpot.defaultBlockState();
        if (!digSpotState.canSurvive(level, digSpotPos)) {
            return false;
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
        return true;
    }
}
