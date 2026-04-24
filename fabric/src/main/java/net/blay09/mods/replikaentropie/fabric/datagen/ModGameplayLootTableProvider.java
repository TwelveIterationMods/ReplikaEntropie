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
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

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
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(6)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(ModItems.scrap))
                        .add(LootItem.lootTableItem(Items.IRON_CHAIN))
                        .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1f, 0.2f))))
                        .add(LootItem.lootTableItem(Items.IRON_BOOTS)
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1f, 0.2f))))
                        .add(LootItem.lootTableItem(ModItems.damagedChipset).setWeight(2))));
    }

}
