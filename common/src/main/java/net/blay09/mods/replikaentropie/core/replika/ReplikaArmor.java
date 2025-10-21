package net.blay09.mods.replikaentropie.core.replika;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ReplikaArmor {

    public static final String TAG_ITEMS = "Items";

    private static boolean testForPart(ItemStack itemStack, Predicate<ItemStack> predicate) {
        if (!(itemStack.is(ModItemTags.REPLIKA_SUIT))) {
            return false;
        }

        for (final var part : getParts(itemStack)) {
            if (predicate.test(part)) {
                return true;
            }
        }

        return false;
    }

    private static boolean testForPart(Player player, ArmorItem.Type armorType, Predicate<ItemStack> predicate) {
        final var itemStack = player.getItemBySlot(armorType.getSlot());
        if (predicate.test(itemStack)) {
            return true;
        }

        return testForPart(itemStack, predicate);
    }

    public static boolean hasPart(Player player, ArmorItem.Type armorType, Item target) {
        return testForPart(player, armorType, it -> it.is(target));
    }

    public static List<ItemStack> getParts(ItemStack itemStack) {
        final var parts = new ArrayList<ItemStack>();
        if (!itemStack.hasTag()) {
            return parts;
        }

        final var itemData = Objects.requireNonNull(itemStack.getTag());
        final var partsTag = itemData.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
        for (final var partTag : partsTag) {
            if (partTag instanceof CompoundTag compoundTag) {
                parts.add(ItemStack.of(compoundTag));
            }
        }
        return parts;
    }

    public static void setParts(ItemStack itemStack, List<ItemStack> parts) {
        final var itemData = itemStack.getOrCreateTag();
        final var partsTag = new ListTag();
        for (final var part : parts) {
            if (!part.isEmpty()) {
                partsTag.add(part.save(new CompoundTag()));
            }
        }
        itemData.put(TAG_ITEMS, partsTag);
    }

    public static ItemStack assembleFrame(ItemStack frameItem) {
        if (frameItem.is(ModItems.replikaHelmetFrame)) {
            return new ItemStack(ModItems.replikaHelmet);
        } else if (frameItem.is(ModItems.replikaChestplateFrame)) {
            return new ItemStack(ModItems.replikaChestplate);
        } else if (frameItem.is(ModItems.replikaLeggingsFrame)) {
            return new ItemStack(ModItems.replikaLeggings);
        } else if (frameItem.is(ModItems.replikaBootsFrame)) {
            return new ItemStack(ModItems.replikaBoots);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isMatchingPart(ItemStack armorItem, ItemStack part) {
        if (armorItem.is(ModItems.replikaHelmetFrame) || armorItem.is(ModItems.replikaHelmet)) {
            return part.is(ModItemTags.REPLIKA_HELMET_PART);
        } else if (armorItem.is(ModItems.replikaChestplateFrame) || armorItem.is(ModItems.replikaChestplate)) {
            return part.is(ModItemTags.REPLIKA_CHESTPLATE_PART);
        } else if (armorItem.is(ModItems.replikaLeggingsFrame) || armorItem.is(ModItems.replikaLeggings)) {
            return part.is(ModItemTags.REPLIKA_LEGGINGS_PART);
        } else if (armorItem.is(ModItems.replikaBootsFrame) || armorItem.is(ModItems.replikaBoots)) {
            return part.is(ModItemTags.REPLIKA_BOOTS_PART);
        }
        return false;
    }
}
