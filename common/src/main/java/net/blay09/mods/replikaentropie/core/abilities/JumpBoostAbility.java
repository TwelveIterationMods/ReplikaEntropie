package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JumpBoostAbility implements Ability {

    public static final JumpBoostAbility INSTANCE = new JumpBoostAbility();
    public static final Identifier ID = id("jump_boost");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 25f;
    }

    @Override
    public void tick(Player player, AbilitySourceContext source) {
        if (!player.level().isClientSide()) {
            if (!player.hasEffect(MobEffects.JUMP_BOOST)) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, -1, 2, false, false));
            }
        }
    }

    @Override
    public void deactivate(Player player, @Nullable AbilitySourceContext source) {
        if (!player.level().isClientSide()) {
            player.removeEffect(MobEffects.JUMP_BOOST);
        }
    }

    @Override
    public boolean isAvailable(ServerPlayer player, AbilitySourceContext source) {
        return AbilityManager.canAffordDurability(source, this);
    }

    @Override
    public boolean canActivate(ServerPlayer player, AbilitySourceContext source) {
        return isAvailable(player, source) && AbilityManager.canAffordDurability(source, this);
    }

    public static void onJumpFromGround(Player player) {
        final var source = AbilityManager.getActiveSource(player, INSTANCE);
        if (source != null) {
            AbilityManager.consumeDurability(player, source, INSTANCE);
        }
    }
}
