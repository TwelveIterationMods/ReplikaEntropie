package net.blay09.mods.replikaentropie.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModBlockTags {
    public static final TagKey<Block> BLOCKS_NULLPHASE = TagKey.create(Registries.BLOCK, id("blocks_nullphase"));
    public static final TagKey<Block> IMMUNE_TO_STOMPING = TagKey.create(Registries.BLOCK, id("immune_to_stomping"));
    public static final TagKey<Block> IMMUNE_TO_WORLD_EATER = TagKey.create(Registries.BLOCK, id("immune_to_world_eater"));
}
