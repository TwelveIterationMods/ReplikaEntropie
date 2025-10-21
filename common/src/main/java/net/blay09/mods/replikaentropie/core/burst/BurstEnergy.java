package net.blay09.mods.replikaentropie.core.burst;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.minecraft.world.entity.player.Player;

public class BurstEnergy {

    public static final float MAX_ENERGY = 100f;
    private static final float REGEN_RATE = 1f;
    public static final int REGEN_THROTTLE_TICKS = 20;

    private static final LocalBurstEnergyManager localManager = new LocalBurstEnergyManager();
    private static final AuthorativeBurstEnergyManager persistentManager = new AuthorativeBurstEnergyManager();

    public static void initialize(BalmEvents events) {
        events.onTickEvent(TickType.ServerPlayer, TickPhase.End, player -> {
            recharge(player);
            persistentManager.syncIfDirty(player);
        });

        events.onEvent(PlayerLoginEvent.class, event -> {
            persistentManager.sync(event.getPlayer());
        });
    }

    public static LocalBurstEnergyManager getLocalManager() {
        return localManager;
    }

    public static BurstEnergyManager getManager(Player player) {
        return player.isLocalPlayer() ? localManager : persistentManager;
    }

    public static boolean consumeEnergy(Player player, float amount) {
        return getManager(player).consumeEnergy(player, amount);
    }

    public static float getEnergy(Player player) {
        return getManager(player).getEnergy(player);
    }

    public static void setEnergy(Player player, float energy) {
        getManager(player).setEnergy(player, energy);
    }

    public static void recharge(Player player) {
        final var manager = getManager(player);

        int cooldown = manager.getRechargeCooldown(player);
        if (cooldown > 0) {
            manager.setRechargeCooldown(player, cooldown - 1);
            return;
        }

        float energy = manager.getEnergy(player);
        if (energy < MAX_ENERGY) {
            energy = Math.min(MAX_ENERGY, energy + REGEN_RATE);
            manager.setEnergy(player, energy);
        }
    }

}
