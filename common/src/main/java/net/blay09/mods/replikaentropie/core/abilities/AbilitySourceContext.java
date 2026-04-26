package net.blay09.mods.replikaentropie.core.abilities;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record AbilitySourceContext(Player player, EquipmentSlot slot, ItemStack stack) {
}
