package net.blay09.mods.replikaentropie.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModItemTags {
    public static final TagKey<Item> PROTECTS_FROM_FRAGMENTAL_WASTE = TagKey.create(Registries.ITEM, id("protects_from_fragmental_waste"));
    public static final TagKey<Item> REPAIRS_REPLIKA_ARMOR = TagKey.create(Registries.ITEM, id("repairs_replika_armor"));
    public static final TagKey<Item> REPAIRS_BIOSTEEL_ARMOR = TagKey.create(Registries.ITEM, id("repairs_biosteel_armor"));
    public static final TagKey<Item> REPAIRS_HAZMAT_ARMOR = TagKey.create(Registries.ITEM, id("repairs_hazmat_armor"));
    public static final TagKey<Item> CHARGEABLE = TagKey.create(Registries.ITEM, id("chargeable"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_MODDABLE = TagKey.create(Registries.ITEM, id("replika_workbench/moddable"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_HEAD = TagKey.create(Registries.ITEM, id("replika_workbench/head"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_CHEST = TagKey.create(Registries.ITEM, id("replika_workbench/chest"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_LEGS = TagKey.create(Registries.ITEM, id("replika_workbench/legs"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_FEET = TagKey.create(Registries.ITEM, id("replika_workbench/feet"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_PARTS = TagKey.create(Registries.ITEM, id("replika_workbench/parts"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_HEAD_PARTS = TagKey.create(Registries.ITEM, id("replika_workbench/head_parts"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_CHEST_PARTS = TagKey.create(Registries.ITEM, id("replika_workbench/chest_parts"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_LEGS_PARTS = TagKey.create(Registries.ITEM, id("replika_workbench/legs_parts"));
    public static final TagKey<Item> REPLIKA_WORKBENCH_FEET_PARTS = TagKey.create(Registries.ITEM, id("replika_workbench/feet_parts"));
    public static final TagKey<Item> BIOMASS_INCUBATOR_SEEDS = TagKey.create(Registries.ITEM, id("biomass_incubator/seeds"));
    public static final TagKey<Item> BIOMASS_INCUBATOR_SOILS = TagKey.create(Registries.ITEM, id("biomass_incubator/soils"));
    public static final TagKey<Item> BIOMASS_INCUBATOR_FARMLAND_LIKE = TagKey.create(Registries.ITEM, id("biomass_incubator/farmland_like"));
}
