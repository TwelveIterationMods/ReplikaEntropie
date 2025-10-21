package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class JumpBoostAbility implements Ability {

    public static final JumpBoostAbility INSTANCE = new JumpBoostAbility();
    public static final ResourceLocation ID = id("jump_boost");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 25f;
    }

    @Override
    public void tick(Player player) {
        if (!player.level().isClientSide) {
            if (!player.hasEffect(MobEffects.JUMP)) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 2, false, false));
            }
        }
    }

    @Override
    public void deactivate(Player player) {
        if (!player.level().isClientSide) {
            player.removeEffect(MobEffects.JUMP);
        }
    }

    @Override
    public boolean isAvailable(ServerPlayer player) {
        return ReplikaArmor.hasPart(player, ArmorItem.Type.BOOTS, ModItems.springBoots)
                && AbilityManager.canAffordBurst(player, this);
    }

    @Override
    public boolean canActivate(ServerPlayer player) {
        return isAvailable(player) && AbilityManager.canAffordBurst(player, this);
    }

    public static void onJumpFromGround(Player player) {
        if (AbilityManager.isAbilityActive(player, INSTANCE)) {
            AbilityManager.consumeBurst(player, INSTANCE);
        }
    }
}

