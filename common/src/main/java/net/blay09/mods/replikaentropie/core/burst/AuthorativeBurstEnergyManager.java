package net.blay09.mods.replikaentropie.core.burst;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.network.protocol.BurstEnergyMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AuthorativeBurstEnergyManager implements BurstEnergyManager {

    private static final String ENERGY_KEY = "BurstEnergy";
    private static final String LAST_SYNCED_ENERGY_KEY = "BurstEnergyLastSynced";
    private static final String REGEN_COOLDOWN_KEY = "BurstEnergyRegenCooldown";
    private static final int SYNC_INTERVAL = 5;

    @Override
    public boolean consumeEnergy(Player player, float amount) {
        if (player.getAbilities().invulnerable) {
            return true;
        }

        final var data = getPersistentData(player);
        float energy = data.contains(ENERGY_KEY) ? data.getFloat(ENERGY_KEY) : BurstEnergy.MAX_ENERGY;

        if (energy >= amount) {
            energy -= amount;
            data.putFloat(ENERGY_KEY, energy);
            data.putInt(REGEN_COOLDOWN_KEY, BurstEnergy.REGEN_THROTTLE_TICKS);
            return true;
        }
        return false;
    }

    @Override
    public float getEnergy(Player player) {
        final var data = getPersistentData(player);
        return data.contains(ENERGY_KEY) ? data.getFloat(ENERGY_KEY) : BurstEnergy.MAX_ENERGY;
    }

    @Override
    public void setEnergy(Player player, float energy) {
        final var data = getPersistentData(player);
        data.putFloat(ENERGY_KEY, Math.max(0, Math.min(BurstEnergy.MAX_ENERGY, energy)));
    }

    @Override
    public int getRechargeCooldown(Player player) {
        final var data = getPersistentData(player);
        return data.getInt(REGEN_COOLDOWN_KEY);
    }

    @Override
    public void setRechargeCooldown(Player player, int cooldown) {
        final var data = getPersistentData(player);
        data.putInt(REGEN_COOLDOWN_KEY, cooldown);
    }

    public void syncIfDirty(ServerPlayer player) {
        if (player.tickCount % SYNC_INTERVAL == 0) {
            final var data = getPersistentData(player);
            final var currentEnergy = data.contains(ENERGY_KEY) ? data.getFloat(ENERGY_KEY) : BurstEnergy.MAX_ENERGY;
            final var lastSyncedEnergy = data.getFloat(LAST_SYNCED_ENERGY_KEY);
            if (currentEnergy != lastSyncedEnergy) {
                data.putFloat(LAST_SYNCED_ENERGY_KEY, currentEnergy);
                sync(player);
            }
        }
    }

    public void sync(ServerPlayer player) {
        final var energy = getEnergy(player);
        Balm.getNetworking().sendTo(player, new BurstEnergyMessage(energy));
    }

    private CompoundTag getPersistentData(Player player) {
        final var data = Balm.getHooks().getPersistentData(player);
        final var modData = data.getCompound(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        return modData;
    }

}
