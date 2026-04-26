package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class LocalAbilityStateManager implements AbilityStateManager {

    private final Set<Identifier> activeAbilities = new HashSet<>();

    @Override
    public boolean isActive(Player player, Ability ability) {
        return activeAbilities.contains(ability.getId());
    }

    @Override
    public void setActive(Player player, Ability ability, boolean active, @Nullable AbilitySourceContext source) {
        final var wasActive = activeAbilities.contains(ability.getId());
        if (active) {
            activeAbilities.add(ability.getId());
        } else {
            activeAbilities.remove(ability.getId());
        }

        if (!wasActive && active) {
            ability.activate(player, source);
        } else if (wasActive && !active) {
            ability.deactivate(player, source);
        }
    }
}
