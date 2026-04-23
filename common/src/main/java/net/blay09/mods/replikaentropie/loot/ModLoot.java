package net.blay09.mods.replikaentropie.loot;

import net.blay09.mods.balm.world.level.storage.loot.BalmLootModifier;
import net.blay09.mods.balm.world.level.storage.loot.BalmLootTables;
import net.blay09.mods.replikaentropie.item.AssemblyTicketItem;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModLoot {

    private static final Set<Identifier> DAMAGED_CHIPSET_TARGETS = Set.of(
            Identifier.withDefaultNamespace("chests/village/village_temple"),
            Identifier.withDefaultNamespace("chests/village/village_cartographer"),
            Identifier.withDefaultNamespace("chests/village/village_desert_house"),
            Identifier.withDefaultNamespace("chests/abandoned_mineshaft"),
            Identifier.withDefaultNamespace("chests/ancient_city"),
            Identifier.withDefaultNamespace("chests/buried_treasure"),
            Identifier.withDefaultNamespace("chests/desert_pyramid"),
            Identifier.withDefaultNamespace("chests/jungle_temple"),
            Identifier.withDefaultNamespace("chests/shipwreck_treasure"),
            Identifier.withDefaultNamespace("chests/simple_dungeon"),
            Identifier.withDefaultNamespace("chests/stronghold_library"),
            Identifier.withDefaultNamespace("chests/underwater_ruin_big"),
            Identifier.withDefaultNamespace("chests/underwater_ruin_small")
    );

    private static final Set<Identifier> CHIPSET_RECIPE_TARGETS = Set.of(
            Identifier.withDefaultNamespace("chests/abandoned_mineshaft"),
            Identifier.withDefaultNamespace("chests/ancient_city"),
            Identifier.withDefaultNamespace("chests/buried_treasure"),
            Identifier.withDefaultNamespace("chests/desert_pyramid"),
            Identifier.withDefaultNamespace("chests/jungle_temple"),
            Identifier.withDefaultNamespace("chests/simple_dungeon"),
            Identifier.withDefaultNamespace("chests/stronghold_library"),
            Identifier.withDefaultNamespace("chests/underwater_ruin_big"),
            Identifier.withDefaultNamespace("chests/underwater_ruin_small")
    );

    private static final float DAMAGED_CHIPSET_CHANCE = 0.6f;
    private static final float CHIPSET_RECIPE_CHANCE = 0.2f;

    public static void initialize(BalmLootTables lootTables) {
        lootTables.registerLootModifier(id("damaged_chipsets"), new BalmLootModifier() {
            @Override
            public void apply(LootContext context, List<ItemStack> loot, @Nullable ResourceKey<LootTable> lootTableId) {
                // TODO This should use a loot table instead of hardcoded chance
                if (lootTableId != null && DAMAGED_CHIPSET_TARGETS.contains(lootTableId.identifier())) {
                    if (context.getRandom().nextFloat() <= DAMAGED_CHIPSET_CHANCE) {
                        loot.add(ModItems.damagedChipset.createStack());
                    }
                }
                if (lootTableId != null && CHIPSET_RECIPE_TARGETS.contains(lootTableId.identifier())) {
                    if (context.getRandom().nextFloat() <= CHIPSET_RECIPE_CHANCE) {
                        final var title = Component.translatable("item.replikaentropie.assembly_ticket.loot.chipset");
                        final var uses = context.getRandom().nextInt(1, 3);
                        loot.add(AssemblyTicketItem.create(title, id("assembler/chipset"), uses));
                    }
                }
            }
        });
    }
}
