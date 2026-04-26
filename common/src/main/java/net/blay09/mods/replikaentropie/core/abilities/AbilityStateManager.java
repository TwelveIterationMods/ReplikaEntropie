package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface AbilityStateManager {
    boolean isActive(Player player, Ability ability);

    void setActive(Player player, Ability ability, boolean active, @Nullable AbilitySourceContext source);
}
