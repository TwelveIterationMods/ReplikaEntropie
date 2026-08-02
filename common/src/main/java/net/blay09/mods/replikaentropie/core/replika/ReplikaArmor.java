package net.blay09.mods.replikaentropie.core.replika;

import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.component.ReplikaParts;
import net.blay09.mods.replikaentropie.core.abilities.AbilitySourceContext;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
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
        return getInstalledParts(itemStack).stream().map(installedPart -> installedPart.part().create()).toList();
    }

    public static List<ReplikaParts.InstalledPart> getInstalledParts(ItemStack itemStack) {
        final var data = itemStack.get(ModDataComponents.replikaParts());
        return data != null ? data.parts() : List.of();
    }

    public static void setParts(ItemStack itemStack, List<ItemStack> parts) {
        final var installedParts = new ArrayList<ReplikaParts.InstalledPart>();
        for (int i = 0; i < parts.size(); i++) {
            final var part = parts.get(i);
            if (!part.isEmpty()) {
                installedParts.add(new ReplikaParts.InstalledPart(i + 1, ItemStackTemplate.fromNonEmptyStack(part)));
            }
        }
        setInstalledParts(itemStack, installedParts);
    }

    public static void setInstalledParts(ItemStack itemStack, List<ReplikaParts.InstalledPart> parts) {
        itemStack.set(ModDataComponents.replikaParts(), new ReplikaParts(parts));
    }


    public static boolean isReplikaArmor(ItemStack itemStack) {
        return itemStack.is(ModItemTags.REPLIKA_WORKBENCH_MODDABLE);
    }

    public static boolean isReplikaPart(ItemStack itemStack) {
        return itemStack.is(ModItemTags.REPLIKA_WORKBENCH_PARTS);
    }

    public static boolean isMatchingPart(ItemStack armorItem, ItemStack part) {
        return (armorItem.is(ModItemTags.REPLIKA_WORKBENCH_HEAD) && part.is(ModItemTags.REPLIKA_WORKBENCH_HEAD_PARTS))
                || (armorItem.is(ModItemTags.REPLIKA_WORKBENCH_CHEST) && part.is(ModItemTags.REPLIKA_WORKBENCH_CHEST_PARTS))
                || (armorItem.is(ModItemTags.REPLIKA_WORKBENCH_LEGS) && part.is(ModItemTags.REPLIKA_WORKBENCH_LEGS_PARTS))
                || (armorItem.is(ModItemTags.REPLIKA_WORKBENCH_FEET) && part.is(ModItemTags.REPLIKA_WORKBENCH_FEET_PARTS));
    }

}
