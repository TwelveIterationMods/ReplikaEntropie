package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class GraviliftAbility implements Ability {

    public static final GraviliftAbility INSTANCE = new GraviliftAbility();
    public static final Identifier ID = id("gravilift");

    protected GraviliftAbility() {
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 0.75f;
    }

    @Override
    public void tick(Player player, AbilitySourceContext source) {
        player.getAbilities().mayfly = true;

        if (player.getAbilities().flying && !AbilityManager.consumeDurability(player, source, this)) {
            deactivate(player, source);
        }
    }

    @Override
    public void activate(Player player, @Nullable AbilitySourceContext source) {
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    @Override
    public void deactivate(Player player, @Nullable AbilitySourceContext source) {
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
    }

    @Override
    public boolean isAvailable(ServerPlayer player, AbilitySourceContext source) {
        if (!AbilityManager.canAffordDurability(source, this)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canActivate(ServerPlayer player, AbilitySourceContext source) {
        return AbilityManager.canAffordDurability(source, this);
    }
}
