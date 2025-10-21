package net.blay09.mods.replikaentropie.core.burst;

import net.minecraft.world.entity.player.Player;

public interface BurstEnergyManager {
    boolean consumeEnergy(Player player, float amount);

    float getEnergy(Player player);

    void setEnergy(Player player, float energy);

    int getRechargeCooldown(Player player);

    void setRechargeCooldown(Player player, int cooldown);
}
