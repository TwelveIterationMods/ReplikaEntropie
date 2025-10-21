package net.blay09.mods.replikaentropie.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> IMMUNE_TO_BIOMASS_HARVESTER = TagKey.create(Registries.ENTITY_TYPE, id("immune_to_biomass_harvester"));
}
