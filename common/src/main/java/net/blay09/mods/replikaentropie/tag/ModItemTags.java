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
    public static final TagKey<Item> COOLS_FRAGMENTAL_GENERATOR = TagKey.create(Registries.ITEM, id("cools_fragmental_generator"));
    public static final TagKey<Item> BIOMASS_INCUBATOR_SEEDS = TagKey.create(Registries.ITEM, id("biomass_incubator/seeds"));
    public static final TagKey<Item> BIOMASS_INCUBATOR_SOILS = TagKey.create(Registries.ITEM, id("biomass_incubator/soils"));
    public static final TagKey<Item> BIOMASS_INCUBATOR_FARMLAND_LIKE = TagKey.create(Registries.ITEM, id("biomass_incubator/farmland_like"));
}
