package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocalAbilityStateManager implements AbilityStateManager {

    private final Set<ResourceLocation> activeAbilities = new HashSet<>();
    private final Map<ResourceLocation, Float> abilityBurstCosts = new HashMap<>();

    @Override
    public boolean isActive(Player player, Ability ability) {
        return activeAbilities.contains(ability.getId());
    }

    @Override
    public void setActive(Player player, Ability ability, boolean active) {
        final var wasActive = activeAbilities.contains(ability.getId());
        if (active) {
            activeAbilities.add(ability.getId());
        } else {
            activeAbilities.remove(ability.getId());
        }

        if (!wasActive && active) {
            ability.activate(player);
        } else if (wasActive && !active) {
            ability.deactivate(player);
        }
    }

    @Override
    public float getBurstCost(Player player, Ability ability) {
        return abilityBurstCosts.getOrDefault(ability.getId(), 0f);
    }

    public void setBurstCost(Player player, Ability ability, float burstCost) {
        abilityBurstCosts.put(ability.getId(), burstCost);
    }
}
