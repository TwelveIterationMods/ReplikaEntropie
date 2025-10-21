package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BurstDrillItem extends DiggerItem {

    private static final float BURST_COST = 1f;
    private static final String TAG_LAST_BLOCK = "BurstDrillLastBlock";
    private static final String TAG_LAST_TIME = "BurstDrillLastTime";
    private static final String TAG_RECENT_COUNT = "BurstDrillRecentCount";
    private static final int MAX_RECENT_MS = 5000;

    private static final int SPEEDUP_SOUND_INTERVAL = 6;

    private static final float MIN_SPEED = Tiers.IRON.getSpeed();
    private static final float MAX_SPEED = Tiers.NETHERITE.getSpeed() * 100;

    public BurstDrillItem(Properties properties) {
        super(1f, -2.8f, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    public static void onClientDestroyBlock(Player player, BlockPos pos) {
        // Simulate power use on client because mineBlock is only called on server
        if (player.getMainHandItem().is(ModItems.burstDrill)) {
            BurstEnergy.consumeEnergy(player, BURST_COST);
        }
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return super.getDestroySpeed(itemStack, state);
        }

        final var itemData = itemStack.getOrCreateTag();
        final var blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        final var lastBlockId = itemData.getString(TAG_LAST_BLOCK);
        if (!blockId.equals(lastBlockId)) {
            return MIN_SPEED;
        }

        final var recentCount = Math.max(0, itemData.getInt(TAG_RECENT_COUNT));
        return Mth.clamp(MIN_SPEED + (recentCount * 0.5f), MIN_SPEED, MAX_SPEED);
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!(entityLiving instanceof Player player)) {
            return super.mineBlock(itemStack, level, state, pos, entityLiving);
        }

        final var canAfford = BurstEnergy.consumeEnergy(player, BURST_COST);

        final var now = System.currentTimeMillis();
        final var blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        final var itemData = itemStack.getOrCreateTag();
        final var lastBlockId = itemData.getString(TAG_LAST_BLOCK);
        final var lastTime = itemData.getLong(TAG_LAST_TIME);
        var recentCount = itemData.getInt(TAG_RECENT_COUNT);

        final var chainReset = !canAfford || (!blockId.equals(lastBlockId) || now - lastTime > MAX_RECENT_MS);
        if (chainReset) {
            if (!level.isClientSide && recentCount > 0) {
                // POSTJAM play a sound here too, but only if recentCount was significant enough
            }
            recentCount = 1;
        } else {
            final int newCount = Math.max(1, recentCount + 1);
            if (!level.isClientSide && newCount % SPEEDUP_SOUND_INTERVAL == 0) {
                final var pitch = Mth.clamp(0.9f + (newCount / (float) SPEEDUP_SOUND_INTERVAL) * 0.03f, 0, 1.26f);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, pitch);
            }
            recentCount = newCount;
        }

        itemData.putString(TAG_LAST_BLOCK, blockId);
        itemData.putLong(TAG_LAST_TIME, now);
        itemData.putInt(TAG_RECENT_COUNT, recentCount);

        return super.mineBlock(itemStack, level, state, pos, entityLiving);
    }

}