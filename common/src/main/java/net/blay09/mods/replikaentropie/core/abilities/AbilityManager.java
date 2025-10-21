package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.network.protocol.AbilityStateMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private static final Map<ResourceLocation, Ability> abilities = new HashMap<>();
    private static final LocalAbilityStateManager localStateManager = new LocalAbilityStateManager();
    private static final AuthoritativeAbilityStateManager authoritativeStateManager = new AuthoritativeAbilityStateManager();

    public static void initialize(BalmEvents events) {
        events.onTickEvent(TickType.ServerPlayer, TickPhase.Start, AbilityManager::serverTick);

        events.onEvent(PlayerLoginEvent.class, event -> {
            final var player = event.getPlayer();
            final var manager = getStateManager(player);
            for (final var ability : abilities.values()) {
                Balm.getNetworking().sendTo(player, new AbilityStateMessage(ability.getId(), manager.isActive(player, ability), manager.getBurstCost(player, ability)));
            }
        });
    }

    public static void registerAbility(Ability ability) {
        abilities.put(ability.getId(), ability);
    }

    public static void clientTick(Player player) {
        final var manager = getStateManager(player);
        for (final var ability : abilities.values()) {
            if (manager.isActive(player, ability)) {
                ability.tick(player);
            } else {
                ability.inactiveTick(player);
            }
        }
    }

    private static void serverTick(ServerPlayer player) {
        final var manager = getStateManager(player);
        for (final var ability : abilities.values()) {
            var isActive = manager.isActive(player, ability);
            if (isActive && !ability.isAvailable(player)) {
                manager.setActive(player, ability, false);
                continue;
            } else if (!isActive && ability.canActivate(player) && ability.isAvailable(player)) {
                manager.setActive(player, ability, true);
                isActive = true;
            }

            if (isActive) {
                ability.tick(player);
            } else {
                ability.inactiveTick(player);
            }
        }
    }

    private static AbilityStateManager getStateManager(Player player) {
        return player.isLocalPlayer() ? localStateManager : authoritativeStateManager;
    }

    public static Ability getAbility(Player player, ResourceLocation id) {
        return abilities.get(id);
    }

    public static boolean isAbilityActive(Player player, Ability ability) {
        return getStateManager(player).isActive(player, ability);
    }

    public static boolean consumeBurst(Player player, Ability ability) {
        final var cost = getStateManager(player).getBurstCost(player, ability);
        return BurstEnergy.consumeEnergy(player, cost);
    }

    public static LocalAbilityStateManager getLocalStateManager() {
        return localStateManager;
    }

    public static boolean canAffordBurst(Player player, Ability ability) {
        return canAffordBurst(player, ability, 1);
    }

    public static boolean canAffordBurst(Player player, Ability ability, int times) {
        final var cost = getStateManager(player).getBurstCost(player, ability);
        return BurstEnergy.getEnergy(player) >= cost * times;
    }
}
