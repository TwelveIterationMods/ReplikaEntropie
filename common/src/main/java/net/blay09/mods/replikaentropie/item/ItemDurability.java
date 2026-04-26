package net.blay09.mods.replikaentropie.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemDurability {

    private ItemDurability() {
    }

    public static boolean hasCharges(ItemStack itemStack) {
        return itemStack.isDamageableItem() && itemStack.getDamageValue() < itemStack.getMaxDamage();
    }

    public static boolean canSpend(ItemStack itemStack, int amount) {
        return itemStack.isDamageableItem() && itemStack.getDamageValue() + amount <= itemStack.getMaxDamage();
    }

    public static boolean spend(ItemStack itemStack, Player player, InteractionHand hand, int amount) {
        if (!canSpend(itemStack, amount)) {
            return false;
        }

        itemStack.hurtAndBreak(amount, player, hand);
        return true;
    }
}
