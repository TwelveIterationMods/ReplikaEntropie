package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.menu.ResearchMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SkyScraperItem extends Item {
    public SkyScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            Balm.networking().openMenu(player, new ResearchMenu.Provider());
        }
        return InteractionResult.SUCCESS;
    }
}
