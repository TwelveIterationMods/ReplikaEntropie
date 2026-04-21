package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BurstDrillItem extends Item {

    private static final float BURST_COST = 1f;
    private static final String TAG_LAST_BLOCK = "BurstDrillLastBlock";
    private static final String TAG_LAST_TIME = "BurstDrillLastTime";
    private static final String TAG_RECENT_COUNT = "BurstDrillRecentCount";
    private static final int MAX_RECENT_MS = 5000;

    private static final int SPEEDUP_SOUND_INTERVAL = 6;

    private static final float MIN_SPEED = ToolMaterial.IRON.speed();
    private static final float MAX_SPEED = ToolMaterial.NETHERITE.speed() * 100;

    public BurstDrillItem(Properties properties) {
        super(properties);
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

        final var itemData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        final var blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        final var lastBlockId = itemData.getString(TAG_LAST_BLOCK).orElse(null);
        if (!blockId.equals(lastBlockId)) {
            return MIN_SPEED;
        }

        final var recentCount = Math.max(0, itemData.getIntOr(TAG_RECENT_COUNT, 0));
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
        final var itemData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        final var lastBlockId = itemData.getString(TAG_LAST_BLOCK).orElse(null);
        final var lastTime = itemData.getLongOr(TAG_LAST_TIME, 0);
        var recentCount = itemData.getIntOr(TAG_RECENT_COUNT, 0);

        final var chainReset = !canAfford || (!blockId.equals(lastBlockId) || now - lastTime > MAX_RECENT_MS);
        if (chainReset) {
            if (!level.isClientSide() && recentCount > 0) {
                // POSTJAM play a sound here too, but only if recentCount was significant enough
            }
            recentCount = 1;
        } else {
            final int newCount = Math.max(1, recentCount + 1);
            if (!level.isClientSide() && newCount % SPEEDUP_SOUND_INTERVAL == 0) {
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