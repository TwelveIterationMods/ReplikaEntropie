package net.blay09.mods.replikaentropie.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModItemTags {
    public static final TagKey<Item> PROTECTS_FROM_FRAGMENTAL_WASTE = TagKey.create(Registries.ITEM, id("protects_from_fragmental_waste"));
    public static final TagKey<Item> REPLIKA_FRAME = TagKey.create(Registries.ITEM, id("replika_frame"));
    public static final TagKey<Item> REPLIKA_SUIT = TagKey.create(Registries.ITEM, id("replika_suit"));
    public static final TagKey<Item> REPLIKA_PART = TagKey.create(Registries.ITEM, id("replika_part"));
    public static final TagKey<Item> REPLIKA_HELMET_PART = TagKey.create(Registries.ITEM, id("replika_helmet_part"));
    public static final TagKey<Item> REPLIKA_CHESTPLATE_PART = TagKey.create(Registries.ITEM, id("replika_chestplate_part"));
    public static final TagKey<Item> REPLIKA_LEGGINGS_PART = TagKey.create(Registries.ITEM, id("replika_leggings_part"));
    public static final TagKey<Item> REPLIKA_BOOTS_PART = TagKey.create(Registries.ITEM, id("replika_boots_part"));
}
