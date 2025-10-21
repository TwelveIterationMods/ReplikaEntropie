package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DataItem extends Item {
    public DataItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (isEdible() && livingEntity instanceof ServerPlayer player) {
            Analyzer.getManager(player).grantData(player, level.random.nextInt(1, 10));
        }
        return super.finishUsingItem(itemStack, level, livingEntity);
    }
}
