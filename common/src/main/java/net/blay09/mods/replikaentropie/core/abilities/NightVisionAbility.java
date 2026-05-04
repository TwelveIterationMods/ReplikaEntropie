package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.replikaentropie.client.handler.PostEffects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class NightVisionAbility implements Ability {

    public static final NightVisionAbility INSTANCE = new NightVisionAbility();
    public static final Identifier ID = id("night_vision");
    public static final Identifier SHADER = Identifier.withDefaultNamespace("creeper");

    protected NightVisionAbility() {
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 0.01f;
    }

    @Override
    public void tick(Player player, AbilitySourceContext source) {
        if (!AbilityManager.consumeDurability(player, source, this)) {
            return;
        }

        if (!player.level().isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                DarknessTracker.tick(serverPlayer);
            }

            if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
            }
        }
    }

    @Override
    public void activate(Player player, @Nullable AbilitySourceContext source) {
        if (player.level().isClientSide()) {
            PostEffects.updatePostEffect();
        }
    }

    @Override
    public void deactivate(Player player, @Nullable AbilitySourceContext source) {
        if (!player.level().isClientSide()) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            if (player instanceof ServerPlayer serverPlayer) {
                DarknessTracker.reset(serverPlayer);
            }
        } else {
            PostEffects.updatePostEffect();
        }
    }
    @Override
    public boolean isAvailable(ServerPlayer player, AbilitySourceContext source) {
        return AbilityManager.canAffordDurability(source, this);
    }

    @Override
    public boolean canActivate(ServerPlayer player, AbilitySourceContext source) {
        return AbilityManager.canAffordDurability(source, this);
    }

    @Override
    public void inactiveTick(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            DarknessTracker.tick(serverPlayer);
        }
    }
}
