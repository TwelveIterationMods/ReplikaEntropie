package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.effect.ModEffects;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class SpeedBoostAbility implements Ability {

    public static final SpeedBoostAbility INSTANCE = new SpeedBoostAbility();
    public static final ResourceLocation ID = id("speed_boost");

    protected SpeedBoostAbility() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 2f;
    }

    @Override
    public void tick(Player player) {
        if (player.isSprinting()) {
            if (AbilityManager.consumeBurst(player, this)) {
                if (!player.level().isClientSide) {
                    if (!player.hasEffect(ModEffects.entropicSpeed)) {
                        player.addEffect(new MobEffectInstance(ModEffects.entropicSpeed, -1, 2, false, false));
                    }
                }
            }
        } else {
            if (!player.level().isClientSide) {
                player.removeEffect(ModEffects.entropicSpeed);
            }
        }
    }

    @Override
    public void deactivate(Player player) {
        if (!player.level().isClientSide) {
            player.removeEffect(ModEffects.entropicSpeed);
        }
    }

    @Override
    public boolean isAvailable(ServerPlayer player) {
        if (!AbilityManager.canAffordBurst(player, this)) {
            return false;
        }

        return ReplikaArmor.hasPart(player, ArmorItem.Type.LEGGINGS, ModItems.semisonicSpeeders);
    }

    @Override
    public boolean canActivate(ServerPlayer player) {
        return AbilityManager.canAffordBurst(player, this);
    }
}
