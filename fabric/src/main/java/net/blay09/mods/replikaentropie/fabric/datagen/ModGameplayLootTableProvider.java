package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModGameplayLootTableProvider extends SimpleFabricLootTableSubProvider {
    public ModGameplayLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, LootContextParamSets.EMPTY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ResourceKey.create(Registries.LOOT_TABLE, id("metal_detector/sand")), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.damagedChipset))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET))
                        .add(LootItem.lootTableItem(Items.IRON_NUGGET))
                        .add(LootItem.lootTableItem(ModItems.scrap))
                        .when(LootItemRandomChanceCondition.randomChance(0.125f))));
    }

}
