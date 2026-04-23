package net.blay09.mods.replikaentropie.core.analyzer;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzedEntitiesMessage;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzedItemsMessage;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzedPlayersMessage;
import net.blay09.mods.replikaentropie.network.protocol.DataCollectedMessage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class PersistentAnalyzerManager implements AnalyzerManager {

    private static final String ANALYZED_ITEMS = "analyzedItems";
    private static final String ANALYZED_PLAYERS = "analyzedPlayers";
    private static final String ANALYZED_ENTITIES = "analyzedEntities";
    private static final String DOWNLOADED_EVENTS = "downloadedEvents";

    private CompoundTag getPersistentData(Player player) {
        final var data = Balm.hooks().getPersistentData(player);
        final var modData = data.getCompoundOrEmpty(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        return modData;
    }

    @Override
    public void analyzeItem(Player player, ItemStack itemStack) {
        final var usedScannerItemStack = player.getUseItem();
        if (itemStack.is(ModBlocks.chaosEngine.asItem()) && usedScannerItemStack.is(ModItems.handheldAnalyzer)) {
            final var usedScannerItem = usedScannerItemStack.getItem();
            usedScannerItemStack.shrink(1);
            player.onEquippedItemBroken(usedScannerItem, player.getUsedItemHand().asEquipmentSlot());
            return;
        }

        if (isAnalyzed(player, itemStack)) {
            return;
        }

        final var dataForItem = ResearchItemRecords.getCollectableData(itemStack);
        if (usedScannerItemStack.is(ModItems.handheldAnalyzer)) {
            grantData(player, dataForItem);
        } else {
            ReplikaEntropie.logger.warn("Tried to analyze item without using an analyzer");
            return;
        }

        final var itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());

        final var data = getPersistentData(player);
        final var analyzedItems = data.getCompoundOrEmpty(ANALYZED_ITEMS);
        analyzedItems.putBoolean(itemId.toString(), true);
        data.put(ANALYZED_ITEMS, analyzedItems);

        Balm.networking().sendTo(player, new AnalyzedItemsMessage(false, List.of(itemStack.getItem())));
    }

    @Override
    public boolean isAnalyzed(Player player, ItemStack itemStack) {
        final var itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        final var analyzedItems = getPersistentData(player).getCompoundOrEmpty(ANALYZED_ITEMS);
        return analyzedItems.contains(itemId.toString());
    }

    @Override
    public void analyzeEntity(Player player, Entity entity) {
        if (isAnalyzed(player, entity)) {
            return;
        }

        final var data = getPersistentData(player);
        if (entity instanceof Player targetPlayer) {
            final var analyzedPlayers = data.getCompoundOrEmpty(ANALYZED_PLAYERS);
            analyzedPlayers.putBoolean(targetPlayer.getUUID().toString(), true);
            data.put(ANALYZED_PLAYERS, analyzedPlayers);

            Balm.networking().sendTo(player, new AnalyzedPlayersMessage(false, List.of(targetPlayer.getUUID())));
        } else {
            final var entityType = entity.getType();
            final var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            final var analyzedEntities = data.getCompoundOrEmpty(ANALYZED_ENTITIES);
            analyzedEntities.putBoolean(entityTypeId.toString(), true);
            data.put(ANALYZED_ENTITIES, analyzedEntities);

            Balm.networking().sendTo(player, new AnalyzedEntitiesMessage(false, List.of(entityType)));
        }
    }

    @Override
    public void grantData(Player player, int amount) {
        final var data = getPersistentData(player);
        final var dataCollected = data.getIntOr("PersonalDataCollected", 0);
        data.putInt("PersonalDataCollected", dataCollected + amount);
        if (amount > 0) {
            Balm.networking().sendTo(player, new DataCollectedMessage(amount, dataCollected + amount));
        }
    }

    @Override
    public boolean isAnalyzed(Player player, Entity entity) {
        final var data = getPersistentData(player);
        if (entity instanceof Player targetPlayer) {
            final var analyzedPlayers = data.getCompoundOrEmpty(ANALYZED_PLAYERS);
            return analyzedPlayers.contains(targetPlayer.getUUID().toString());
        } else {
            final var entityType = entity.getType();
            final var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            final var analyzedEntities = data.getCompoundOrEmpty(ANALYZED_ENTITIES);
            return analyzedEntities.contains(entityTypeId.toString());
        }
    }

    public void sendAllToPlayer(ServerPlayer player) {
        final var data = getPersistentData(player);
        final var analyzedItemIds = data.getCompoundOrEmpty(ANALYZED_ITEMS);
        final var analyzedItems = analyzedItemIds.keySet().stream()
                .map(Identifier::parse)
                .map(BuiltInRegistries.ITEM::getValue)
                .toList();
        Balm.networking().sendTo(player, new AnalyzedItemsMessage(true, analyzedItems));

        final var analyzedPlayerIds = data.getCompoundOrEmpty(ANALYZED_PLAYERS);
        final var analyzedPlayers = analyzedPlayerIds.keySet().stream()
                .map(UUID::fromString)
                .toList();
        Balm.networking().sendTo(player, new AnalyzedPlayersMessage(true, analyzedPlayers));

        final var analyzedEntityIds = data.getCompoundOrEmpty(ANALYZED_ENTITIES);
        final var analyzedEntities = analyzedEntityIds.keySet().stream()
                .map(Identifier::parse)
                .map(BuiltInRegistries.ENTITY_TYPE::getValue)
                .toList();
        Balm.networking().sendTo(player, new AnalyzedEntitiesMessage(true, analyzedEntities));
    }

    @Override
    public void resetAll(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_ITEMS);
        data.remove(ANALYZED_PLAYERS);
        data.remove(ANALYZED_ENTITIES);
        Balm.networking().sendTo(player, new AnalyzedItemsMessage(true, List.of()));
        Balm.networking().sendTo(player, new AnalyzedPlayersMessage(true, List.of()));
        Balm.networking().sendTo(player, new AnalyzedEntitiesMessage(true, List.of()));
    }

    @Override
    public void resetAnalyzedItems(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_ITEMS);
        Balm.networking().sendTo(player, new AnalyzedItemsMessage(true, List.of()));
    }

    @Override
    public void resetAnalyzedPlayers(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_PLAYERS);
        Balm.networking().sendTo(player, new AnalyzedPlayersMessage(true, List.of()));
    }

    @Override
    public void resetAnalyzedEntities(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_ENTITIES);
        Balm.networking().sendTo(player, new AnalyzedEntitiesMessage(true, List.of()));
    }

    @Override
    public int getDataCollected(Player player) {
        final var data = getPersistentData(player);
        return data.getIntOr("PersonalDataCollected", 0);
    }

    public boolean isDataMinedEventDownloaded(Player player, DataMinedEvent event) {
        final var data = getPersistentData(player);
        final var downloaded = data.getCompoundOrEmpty(DOWNLOADED_EVENTS);
        return downloaded.getBooleanOr(event.asKey(), false);
    }

    public void downloadDataMinedEvent(Player player, DataMinedEvent event) {
        final var data = getPersistentData(player);
        final var downloaded = data.getCompoundOrEmpty(DOWNLOADED_EVENTS);
        downloaded.putBoolean(event.asKey(), true);
        data.put(DOWNLOADED_EVENTS, downloaded);

        grantData(player, event.dataMined());
    }

    public void resetDataMinedEvents(Player player) {
        final var data = getPersistentData(player);
        data.remove(DOWNLOADED_EVENTS);
    }
}
