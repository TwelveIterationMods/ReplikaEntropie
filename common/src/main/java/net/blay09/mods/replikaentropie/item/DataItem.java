package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DataItem extends Item {
    public DataItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create(DataMinedEvent event) {
        return ModItems.data.createStack(event.dataMined());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            Analyzer.getManager(player).grantData(player, 1);
        }
        return super.finishUsingItem(itemStack, level, livingEntity);
    }
}
