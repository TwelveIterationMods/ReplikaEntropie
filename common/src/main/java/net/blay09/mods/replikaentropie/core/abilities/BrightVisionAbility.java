package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BrightVisionAbility implements Ability {

    public static final BrightVisionAbility INSTANCE = new BrightVisionAbility();
    public static final Identifier ID = id("bright_vision");

    protected BrightVisionAbility() {
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
    public void deactivate(Player player, @Nullable AbilitySourceContext source) {
        if (!player.level().isClientSide()) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            if (player instanceof ServerPlayer serverPlayer) {
                DarknessTracker.reset(serverPlayer);
            }
        }
    }

    @Override
    public boolean isAvailable(ServerPlayer player, AbilitySourceContext source) {
        if (!AbilityManager.canAffordDurability(source, this)) {
            return false;
        }

        if (DarknessTracker.isWithTheLight(player)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canActivate(ServerPlayer player, AbilitySourceContext source) {
        return AbilityManager.canAffordDurability(source, this)
                && DarknessTracker.isInTheDark(player);
    }

    @Override
    public void inactiveTick(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            DarknessTracker.tick(serverPlayer);
        }
    }
}
