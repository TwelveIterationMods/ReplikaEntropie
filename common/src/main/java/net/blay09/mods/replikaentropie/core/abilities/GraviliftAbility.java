package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class GraviliftAbility implements Ability {

    public static final GraviliftAbility INSTANCE = new GraviliftAbility();
    public static final ResourceLocation ID = id("gravilift");

    protected GraviliftAbility() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getDefaultBurstCost() {
        return 0.75f;
    }

    @Override
    public void tick(Player player) {
        player.getAbilities().mayfly = true;

        if (player.getAbilities().flying) {
            BurstEnergy.consumeEnergy(player, getDefaultBurstCost());
        }
    }

    @Override
    public void activate(Player player) {
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    @Override
    public void deactivate(Player player) {
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
    }

    @Override
    public boolean isAvailable(ServerPlayer player) {
        if (!AbilityManager.canAffordBurst(player, this)) {
            return false;
        }

        return ReplikaArmor.hasPart(player, ArmorItem.Type.CHESTPLATE, ModItems.graviliftHarness);
    }

    @Override
    public boolean canActivate(ServerPlayer player) {
        return BurstEnergy.getEnergy(player) >= getDefaultBurstCost();
    }
}
