package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.world.entity.player.Player;

public interface AbilityStateManager {
    boolean isActive(Player player, Ability ability);

    void setActive(Player player, Ability ability, boolean active);

    float getBurstCost(Player player, Ability ability);
}
