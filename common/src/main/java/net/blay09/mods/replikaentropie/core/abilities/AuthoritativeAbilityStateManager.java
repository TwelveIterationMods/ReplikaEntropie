package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.network.protocol.AbilityStateMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class AuthoritativeAbilityStateManager implements AbilityStateManager {

    private static final String ABILITY_STATES = "AbilityStates";

    @Override
    public boolean isActive(Player player, Ability ability) {
        final var data = getPersistentData(player);
        final var abilityStates = data.getCompoundOrEmpty(ABILITY_STATES);
        return abilityStates.getBooleanOr(ability.getId().toString(), false);
    }

    @Override
    public void setActive(Player player, Ability ability, boolean active) {
        final var data = getPersistentData(player);
        final var abilityStates = data.getCompoundOrEmpty(ABILITY_STATES);
        data.put(ABILITY_STATES, abilityStates);
        final var abilityKey = ability.getId().toString();
        final var wasActive = abilityStates.getBooleanOr(abilityKey, false);
        abilityStates.putBoolean(abilityKey, active);

        if (!wasActive && active) {
            ability.activate(player);
        } else if(wasActive && !active) {
            ability.deactivate(player);
        }

        Balm.networking().sendTo(player, new AbilityStateMessage(ability.getId(), active, ability.getDefaultBurstCost()));
    }

    @Override
    public float getBurstCost(Player player, Ability ability) {
        return ability.getDefaultBurstCost();
    }

    private static CompoundTag getPersistentData(Player player) {
        final var data = Balm.hooks().getPersistentData(player);
        final var modData = data.getCompoundOrEmpty(ReplikaEntropie.MOD_ID);
        data.put(ReplikaEntropie.MOD_ID, modData);
        return modData;
    }
}
