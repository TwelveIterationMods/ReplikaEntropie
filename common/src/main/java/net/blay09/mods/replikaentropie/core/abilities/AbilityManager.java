package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.network.protocol.AbilityStateMessage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private static final Map<Identifier, Ability> abilities = new HashMap<>();
    private static final LocalAbilityStateManager localStateManager = new LocalAbilityStateManager();
    private static final AuthoritativeAbilityStateManager authoritativeStateManager = new AuthoritativeAbilityStateManager();

    public static void initialize() {
        ServerTickCallback.ServerPlayerTick.BEFORE.register(AbilityManager::serverTick);

        ServerPlayerCallback.Join.EVENT.register(player -> {
            final var manager = getStateManager(player);
            for (final var ability : abilities.values()) {
                Balm.networking().sendTo(player, new AbilityStateMessage(ability.getId(), manager.isActive(player, ability)));
            }
        });
    }

    public static void registerAbility(Ability ability) {
        abilities.put(ability.getId(), ability);
    }

    public static void clientTick(Player player) {
        final var manager = getStateManager(player);
        for (final var ability : abilities.values()) {
            final var source = resolveSource(player, ability);
            if (manager.isActive(player, ability) && source != null) {
                ability.tick(player, source);
            } else {
                ability.inactiveTick(player);
            }
        }
    }

    private static void serverTick(ServerPlayer player) {
        final var manager = getStateManager(player);
        for (final var ability : abilities.values()) {
            final var source = resolveSource(player, ability);
            var isActive = manager.isActive(player, ability);
            if (isActive && (source == null || !ability.isAvailable(player, source))) {
                manager.setActive(player, ability, false, source);
                continue;
            } else if (!isActive && source != null && ability.canActivate(player, source) && ability.isAvailable(player, source)) {
                manager.setActive(player, ability, true, source);
                isActive = true;
            }

            if (isActive && source != null) {
                ability.tick(player, source);
            } else {
                ability.inactiveTick(player);
            }
        }
    }

    private static AbilityStateManager getStateManager(Player player) {
        return player.isLocalPlayer() ? localStateManager : authoritativeStateManager;
    }

    public static Ability getAbility(Player player, Identifier id) {
        return abilities.get(id);
    }

    public static boolean isAbilityActive(Player player, Ability ability) {
        return getStateManager(player).isActive(player, ability);
    }

    @Nullable
    public static AbilitySourceContext resolveSource(Player player, Ability ability) {
        for (final var slot : EquipmentSlot.values()) {
            final var itemStack = player.getItemBySlot(slot);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (hasAbility(itemStack, ability.getId())) {
                return new AbilitySourceContext(player, slot, itemStack);
            }
        }

        return null;
    }

    private static boolean hasAbility(ItemStack itemStack, Identifier abilityId) {
        final var abilityHolder = itemStack.get(ModDataComponents.abilityHolder());
        if (abilityHolder != null && abilityHolder.hasAbility(abilityId)) {
            return true;
        }

        for (final var part : ReplikaArmor.getParts(itemStack)) {
            final var partAbilityHolder = part.get(ModDataComponents.abilityHolder());
            if (partAbilityHolder != null && partAbilityHolder.hasAbility(abilityId)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static AbilitySourceContext getActiveSource(Player player, Ability ability) {
        if (!isAbilityActive(player, ability)) {
            return null;
        }
        return resolveSource(player, ability);
    }

    public static boolean consumeDurability(Player player, AbilitySourceContext source, Ability ability) {
        return consumeDurability(player, source, ability.getDefaultBurstCost());
    }

    public static boolean consumeDurability(Player player, AbilitySourceContext source, float burstCost) {
        return AbilityDurability.consume(player, new AbilityCost(source, burstCost));
    }

    public static LocalAbilityStateManager getLocalStateManager() {
        return localStateManager;
    }

    public static boolean canAffordDurability(AbilitySourceContext source, Ability ability) {
        return canAffordDurability(source, ability.getDefaultBurstCost());
    }

    public static boolean canAffordDurability(AbilitySourceContext source, float burstCost) {
        return AbilityDurability.canAfford(new AbilityCost(source, burstCost));
    }
}
