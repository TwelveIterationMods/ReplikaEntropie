package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.replikaentropie.client.handler.PostEffects;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class NightVisionAbility implements Ability {

    public static final NightVisionAbility INSTANCE = new NightVisionAbility();
    public static final ResourceLocation ID = id("night_vision");
    public static final ResourceLocation SHADER = new ResourceLocation("shaders/post/creeper.json");

    protected NightVisionAbility() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 0.01f;
    }

    @Override
    public void tick(Player player) {
        if (!AbilityManager.consumeBurst(player, this)) {
            return;
        }

        if (!player.level().isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                DarknessTracker.tick(serverPlayer);
            }

            if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
            }
        }
    }

    @Override
    public void activate(Player player) {
        if (player.level().isClientSide) {
            PostEffects.updatePostEffect();
        }
    }

    @Override
    public void deactivate(Player player) {
        if (!player.level().isClientSide) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            if (player instanceof ServerPlayer serverPlayer) {
                DarknessTracker.reset(serverPlayer);
            }
        } else {
            PostEffects.updatePostEffect();
        }
    }
    @Override
    public boolean isAvailable(ServerPlayer player) {
        if (!AbilityManager.canAffordBurst(player, this)) {
            return false;
        }

        if (DarknessTracker.isWithTheLight(player)) {
            return false;
        }

        return ReplikaArmor.hasPart(player, ArmorItem.Type.HELMET, ModItems.nightVisionGoggles);
    }

    @Override
    public boolean canActivate(ServerPlayer player) {
        return BurstEnergy.getEnergy(player) >= BurstEnergy.MAX_ENERGY / 2f
                && DarknessTracker.isInTheDark(player);
    }

    @Override
    public void inactiveTick(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            DarknessTracker.tick(serverPlayer);
        }
    }
}
