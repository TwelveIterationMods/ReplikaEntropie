package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface Ability {
    
    Identifier getId();
    
    float getDefaultBurstCost();
    
    default void tick(Player player) {}

    default void inactiveTick(Player player) {}

    default void activate(Player player) {}

    default void deactivate(Player player) {}

    boolean isAvailable(ServerPlayer player);

    default boolean canActivate(ServerPlayer player) {
        return true;
    }
}
