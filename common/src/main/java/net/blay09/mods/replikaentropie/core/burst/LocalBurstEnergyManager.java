package net.blay09.mods.replikaentropie.core.burst;

import net.minecraft.world.entity.player.Player;

public class LocalBurstEnergyManager implements BurstEnergyManager {

    private float energy = BurstEnergy.MAX_ENERGY;
    private int cooldown;

    @Override
    public boolean consumeEnergy(Player player, float amount) {
        if (player.getAbilities().invulnerable) {
            return true;
        }

        if (energy >= amount) {
            energy -= amount;
            cooldown = BurstEnergy.REGEN_THROTTLE_TICKS;
            return true;
        }
        return false;
    }

    @Override
    public float getEnergy(Player player) {
        return energy;
    }

    @Override
    public void setEnergy(Player player, float energy) {
        this.energy = Math.max(0, Math.min(BurstEnergy.MAX_ENERGY, energy));
    }

    @Override
    public int getRechargeCooldown(Player player) {
        return cooldown;
    }

    @Override
    public void setRechargeCooldown(Player player, int cooldown) {
        this.cooldown = cooldown;
    }

    public float getMaxEnergy() {
        return BurstEnergy.MAX_ENERGY;
    }
}
