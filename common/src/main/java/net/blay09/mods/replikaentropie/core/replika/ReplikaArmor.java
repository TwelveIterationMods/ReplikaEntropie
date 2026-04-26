package net.blay09.mods.replikaentropie.core.replika;

import net.blay09.mods.replikaentropie.core.abilities.AbilitySourceContext;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.component.ReplikaParts;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReplikaArmor {

    private static boolean testForPart(ItemStack itemStack, Predicate<ItemStack> predicate) {
        for (final var part : getParts(itemStack)) {
            if (predicate.test(part)) {
                return true;
            }
        }

        return false;
    }

    private static boolean testForPart(Player player, EquipmentSlot equipmentSlot, Predicate<ItemStack> predicate) {
        final var itemStack = player.getItemBySlot(equipmentSlot);
        if (predicate.test(itemStack)) {
            return true;
        }

        return testForPart(itemStack, predicate);
    }

    public static boolean hasPart(Player player, EquipmentSlot equipmentSlot, ItemLike target) {
        return testForPart(player, equipmentSlot, it -> it.is(target.asItem()));
    }

    @Nullable
    public static AbilitySourceContext findAbilitySource(Player player, EquipmentSlot equipmentSlot, ItemLike target) {
        final var itemStack = player.getItemBySlot(equipmentSlot);
        if (itemStack.isEmpty()) {
            return null;
        }

        if (itemStack.is(target.asItem()) || testForPart(itemStack, it -> it.is(target.asItem()))) {
            return new AbilitySourceContext(player, equipmentSlot, itemStack);
        }

        return null;
    }

    public static List<ItemStack> getParts(ItemStack itemStack) {
        final var data = itemStack.get(ModDataComponents.replikaParts());
        return data != null ? data.parts().stream().map(ItemStackTemplate::create).toList() : List.of();
    }

    public static void setParts(ItemStack itemStack, List<ItemStack> parts) {
        final var storedParts = new ArrayList<ItemStackTemplate>();
        for (final var part : parts) {
            if (!part.isEmpty()) {
                storedParts.add(ItemStackTemplate.fromNonEmptyStack(part));
            }
        }
        itemStack.set(ModDataComponents.replikaParts(), new ReplikaParts(storedParts));
    }

    public static ItemStack assembleFrame(ItemStack frameItem) {
        return ItemStack.EMPTY;
    }

    public static boolean isMatchingPart(ItemStack armorItem, ItemStack part) {
        return false;
    }
}
