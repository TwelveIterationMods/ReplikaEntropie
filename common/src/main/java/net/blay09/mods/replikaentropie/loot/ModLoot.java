package net.blay09.mods.replikaentropie.loot;

import net.blay09.mods.balm.world.level.storage.loot.BalmLootModifier;
import net.blay09.mods.balm.world.level.storage.loot.BalmLootTables;
import net.blay09.mods.replikaentropie.block.entity.DigSpotBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModLoot {

    private static final ThreadLocal<Boolean> isApplyingDigSpotLoot = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> isApplyingDamagedChipsetLoot = ThreadLocal.withInitial(() -> false);

    private static final ResourceKey<LootTable> DAMAGED_CHIPSET_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, id("injected/damaged_chipsets"));
    private static final ResourceKey<LootTable> CHIPSET_RECIPE_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, id("injected/chipset_recipe"));

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

    public static void initialize(BalmLootTables lootTables) {
        lootTables.registerLootModifier(id("dig_spots"), new BalmLootModifier() {
            @Override
            public void apply(LootContext context, List<ItemStack> loot, @Nullable ResourceKey<LootTable> lootTableId) {
                if (isApplyingDigSpotLoot.get()) {
                    return;
                }

                final var blockEntity = context.getOptional(LootContextParams.BLOCK_ENTITY);
                if (!(blockEntity instanceof DigSpotBlockEntity digSpotBlockEntity) || digSpotBlockEntity.getLootTable() == null) {
                    return;
                }

                isApplyingDigSpotLoot.set(true);
                try {
                    final var digSpotLootTable = context.getLevel().getServer().reloadableRegistries().getLootTable(digSpotBlockEntity.getLootTable());
                    digSpotLootTable.getRandomItems(context, loot::add);
                } finally {
                    isApplyingDigSpotLoot.set(false);
                }
            }
        });

        lootTables.registerLootModifier(id("damaged_chipsets"), new BalmLootModifier() {
            @Override
            public void apply(LootContext context, List<ItemStack> loot, @Nullable ResourceKey<LootTable> lootTableId) {
                if (isApplyingDamagedChipsetLoot.get()) {
                    return;
                }

                isApplyingDamagedChipsetLoot.set(true);
                try {
                    if (lootTableId != null && DAMAGED_CHIPSET_TARGETS.contains(lootTableId.identifier())) {
                        final var damagedChipsetLootTable = context.getLevel().getServer().reloadableRegistries().getLootTable(DAMAGED_CHIPSET_LOOT_TABLE);
                        damagedChipsetLootTable.getRandomItems(context, loot::add);
                    }
                    if (lootTableId != null && CHIPSET_RECIPE_TARGETS.contains(lootTableId.identifier())) {
                        final var chipsetRecipeLootTable = context.getLevel().getServer().reloadableRegistries().getLootTable(CHIPSET_RECIPE_LOOT_TABLE);
                        chipsetRecipeLootTable.getRandomItems(context, loot::add);
                    }
                } finally {
                    isApplyingDamagedChipsetLoot.set(false);
                }
            }
        });
    }
}
