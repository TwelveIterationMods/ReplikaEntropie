package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class AbilityDurability {

    private AbilityDurability() {
    }

    public static boolean consume(Player player, AbilityCost abilityCost) {
        final var source = abilityCost.source();
        final var cost = getDurabilityCostForCurrentTick(player, abilityCost.burstCost());
        if (cost <= 0) {
            return true;
        }

        if (!canDamage(source.stack(), cost)) {
            return false;
        }

        if (!player.isLocalPlayer()) {
            source.stack().hurtAndBreak(cost, player, source.slot());
        }
        return true;
    }

    public static boolean canAfford(AbilityCost abilityCost) {
        final var cost = getDurabilityCostForNextPayment(abilityCost.burstCost());
        return cost <= 0 || canDamage(abilityCost.source().stack(), cost);
    }

    private static int getDurabilityCostForNextPayment(float cost) {
        if (cost <= 0f) {
            return 0;
        }
        if (cost < 1f) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil(cost));
    }

    private static int getDurabilityCostForCurrentTick(Player player, float cost) {
        if (cost <= 0f) {
            return 0;
        }
        if (cost < 1f) {
            final var interval = Math.max(1, Math.round(1f / cost));
            return player.tickCount % interval == 0 ? 1 : 0;
        }
        return Math.max(1, (int) Math.ceil(cost));
    }

    private static boolean canDamage(ItemStack itemStack, int amount) {
        return itemStack.isDamageableItem() && itemStack.getDamageValue() + amount <= itemStack.getMaxDamage();
    }
}
