package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.menu.ResearchMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkyScraperItem extends Item {
    public SkyScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            Balm.getNetworking().openMenu(player, new ResearchMenu.Provider());
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
