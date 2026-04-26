package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.replikaentropie.effect.ModEffects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class SpeedBoostAbility implements Ability {

    public static final SpeedBoostAbility INSTANCE = new SpeedBoostAbility();
    public static final Identifier ID = id("speed_boost");

    protected SpeedBoostAbility() {
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 2f;
    }

    @Override
    public void tick(Player player, AbilitySourceContext source) {
        if (player.isSprinting()) {
            if (AbilityManager.consumeDurability(player, source, this)) {
                if (!player.level().isClientSide()) {
                    if (!player.hasEffect(ModEffects.entropicSpeed)) {
                        player.addEffect(new MobEffectInstance(ModEffects.entropicSpeed, -1, 2, false, false));
                    }
                }
            }
        } else {
            if (!player.level().isClientSide()) {
                player.removeEffect(ModEffects.entropicSpeed);
            }
        }
    }

    @Override
    public void deactivate(Player player, @Nullable AbilitySourceContext source) {
        if (!player.level().isClientSide()) {
            player.removeEffect(ModEffects.entropicSpeed);
        }
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
