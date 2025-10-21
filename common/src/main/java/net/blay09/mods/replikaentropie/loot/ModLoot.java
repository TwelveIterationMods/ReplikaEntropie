package net.blay09.mods.replikaentropie.loot;

import net.blay09.mods.balm.api.loot.BalmLootTables;
import net.blay09.mods.replikaentropie.item.AssemblyTicketItem;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.util.UnpackedLootTableHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Set;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModLoot {

    private static final Set<ResourceLocation> DAMAGED_CHIPSET_TARGETS = Set.of(
            new ResourceLocation("minecraft:chests/village/village_temple"),
            new ResourceLocation("minecraft:chests/village/village_cartographer"),
            new ResourceLocation("minecraft:chests/village/village_desert_house"),
            new ResourceLocation("minecraft:chests/abandoned_mineshaft"),
            new ResourceLocation("minecraft:chests/ancient_city"),
            new ResourceLocation("minecraft:chests/buried_treasure"),
            new ResourceLocation("minecraft:chests/desert_pyramid"),
            new ResourceLocation("minecraft:chests/jungle_temple"),
            new ResourceLocation("minecraft:chests/shipwreck_treasure"),
            new ResourceLocation("minecraft:chests/simple_dungeon"),
            new ResourceLocation("minecraft:chests/stronghold_library"),
            new ResourceLocation("minecraft:chests/underwater_ruin_big"),
            new ResourceLocation("minecraft:chests/underwater_ruin_small")
    );

    private static final Set<ResourceLocation> CHIPSET_RECIPE_TARGETS = Set.of(
            new ResourceLocation("minecraft:chests/abandoned_mineshaft"),
            new ResourceLocation("minecraft:chests/ancient_city"),
            new ResourceLocation("minecraft:chests/buried_treasure"),
            new ResourceLocation("minecraft:chests/desert_pyramid"),
            new ResourceLocation("minecraft:chests/jungle_temple"),
            new ResourceLocation("minecraft:chests/simple_dungeon"),
            new ResourceLocation("minecraft:chests/stronghold_library"),
            new ResourceLocation("minecraft:chests/underwater_ruin_big"),
            new ResourceLocation("minecraft:chests/underwater_ruin_small")
    );

    private static final float DAMAGED_CHIPSET_CHANCE = 0.6f;
    private static final float CHIPSET_RECIPE_CHANCE = 0.2f;

    public static void initialize(BalmLootTables lootTables) {
        lootTables.registerLootModifier(id("damaged_chipsets"), (context, loot) -> {
            if (!context.hasParam(LootContextParams.ORIGIN)) {
                return;
            }

            final var blockEntity = context.getLevel().getBlockEntity(BlockPos.containing(context.getParam(LootContextParams.ORIGIN)));
            if (blockEntity instanceof UnpackedLootTableHolder unpackedLootTableHolder) {
                unpackedLootTableHolder.getUnpackedLootTable().ifPresent(lootTableId -> {
                    if (DAMAGED_CHIPSET_TARGETS.contains(lootTableId)) {
                        if (context.getRandom().nextFloat() <= DAMAGED_CHIPSET_CHANCE) {
                            loot.add(new ItemStack(ModItems.damagedChipset));
                        }
                    }
                    if (CHIPSET_RECIPE_TARGETS.contains(lootTableId)) {
                        if (context.getRandom().nextFloat() <= CHIPSET_RECIPE_CHANCE) {
                            final var title = Component.translatable("item.replikaentropie.assembly_ticket.loot.chipset");
                            final var uses = context.getRandom().nextInt(1, 3);
                            loot.add(AssemblyTicketItem.create(title, id("assembler/chipset"), uses));
                        }
                    }
                });
            }
        });
    }
}
