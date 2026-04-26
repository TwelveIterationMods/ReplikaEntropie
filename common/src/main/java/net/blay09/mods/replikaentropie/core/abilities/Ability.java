package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface Ability {

    Identifier getId();

    float getDefaultBurstCost();

    default void tick(Player player, AbilitySourceContext source) {}

    default void inactiveTick(Player player) {}

    default void activate(Player player, @Nullable AbilitySourceContext source) {}

    default void deactivate(Player player, @Nullable AbilitySourceContext source) {}

    boolean isAvailable(ServerPlayer player, AbilitySourceContext source);

    default boolean canActivate(ServerPlayer player, AbilitySourceContext source) {
        return true;
    }
}
