package net.blay09.mods.replikaentropie.core.analyzer;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzedEntitiesMessage;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzedItemsMessage;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzedPlayersMessage;
import net.blay09.mods.replikaentropie.network.protocol.DataCollectedMessage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
        final var data = Balm.getHooks().getPersistentData(player);
        final var modData = data.getCompound(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        return modData;
    }

    @Override
    public void analyzeItem(Player player, ItemStack itemStack) {
        if (isAnalyzed(player, itemStack)) {
            return;
        }

        final var dataForItem = ResearchItemRecords.getCollectableData(itemStack);
        if (player.getUseItem().is(ModItems.handheldAnalyzer)) {
            grantData(player, dataForItem);
        } else {
            ReplikaEntropie.logger.warn("Tried to analyze item without using an analyzer");
            return;
        }

        final var itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());

        final var data = getPersistentData(player);
        final var analyzedItems = data.getCompound(ANALYZED_ITEMS);
        analyzedItems.putBoolean(itemId.toString(), true);
        data.put(ANALYZED_ITEMS, analyzedItems);

        Balm.getNetworking().sendTo(player, new AnalyzedItemsMessage(false, List.of(itemStack.getItem())));
    }

    @Override
    public boolean isAnalyzed(Player player, ItemStack itemStack) {
        final var itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        final var analyzedItems = getPersistentData(player).getCompound(ANALYZED_ITEMS);
        return analyzedItems.contains(itemId.toString());
    }

    @Override
    public void analyzeEntity(Player player, Entity entity) {
        if (isAnalyzed(player, entity)) {
            return;
        }

        final var data = getPersistentData(player);
        if (entity instanceof Player targetPlayer) {
            final var analyzedPlayers = data.getCompound(ANALYZED_PLAYERS);
            analyzedPlayers.putBoolean(targetPlayer.getUUID().toString(), true);
            data.put(ANALYZED_PLAYERS, analyzedPlayers);

            Balm.getNetworking().sendTo(player, new AnalyzedPlayersMessage(false, List.of(targetPlayer.getUUID())));
        } else {
            final var entityType = entity.getType();
            final var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            final var analyzedEntities = data.getCompound(ANALYZED_ENTITIES);
            analyzedEntities.putBoolean(entityTypeId.toString(), true);
            data.put(ANALYZED_ENTITIES, analyzedEntities);

            Balm.getNetworking().sendTo(player, new AnalyzedEntitiesMessage(false, List.of(entityType)));
        }
    }

    @Override
    public void grantData(Player player, int amount) {
        final var data = getPersistentData(player);
        final var dataCollected = data.getInt("PersonalDataCollected");
        data.putInt("PersonalDataCollected", dataCollected + amount);
        if (amount > 0) {
            Balm.getNetworking().sendTo(player, new DataCollectedMessage(amount, dataCollected + amount));
        }
    }

    @Override
    public boolean isAnalyzed(Player player, Entity entity) {
        final var data = getPersistentData(player);
        if (entity instanceof Player targetPlayer) {
            final var analyzedPlayers = data.getCompound(ANALYZED_PLAYERS);
            return analyzedPlayers.contains(targetPlayer.getUUID().toString());
        } else {
            final var entityType = entity.getType();
            final var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            final var analyzedEntities = data.getCompound(ANALYZED_ENTITIES);
            return analyzedEntities.contains(entityTypeId.toString());
        }
    }

    public void sendAllToPlayer(ServerPlayer player) {
        final var data = getPersistentData(player);
        final var analyzedItemIds = data.getCompound(ANALYZED_ITEMS);
        final var analyzedItems = analyzedItemIds.getAllKeys().stream()
                .map(ResourceLocation::new)
                .map(BuiltInRegistries.ITEM::get)
                .toList();
        Balm.getNetworking().sendTo(player, new AnalyzedItemsMessage(true, analyzedItems));

        final var analyzedPlayerIds = data.getCompound(ANALYZED_PLAYERS);
        final var analyzedPlayers = analyzedPlayerIds.getAllKeys().stream()
                .map(UUID::fromString)
                .toList();
        Balm.getNetworking().sendTo(player, new AnalyzedPlayersMessage(true, analyzedPlayers));

        final var analyzedEntityIds = data.getCompound(ANALYZED_ENTITIES);
        final var analyzedEntities = analyzedEntityIds.getAllKeys().stream()
                .map(ResourceLocation::new)
                .map(BuiltInRegistries.ENTITY_TYPE::get)
                .toList();
        Balm.getNetworking().sendTo(player, new AnalyzedEntitiesMessage(true, analyzedEntities));
    }

    @Override
    public void resetAll(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_ITEMS);
        data.remove(ANALYZED_PLAYERS);
        data.remove(ANALYZED_ENTITIES);
        Balm.getNetworking().sendTo(player, new AnalyzedItemsMessage(true, List.of()));
        Balm.getNetworking().sendTo(player, new AnalyzedPlayersMessage(true, List.of()));
        Balm.getNetworking().sendTo(player, new AnalyzedEntitiesMessage(true, List.of()));
    }

    @Override
    public void resetAnalyzedItems(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_ITEMS);
        Balm.getNetworking().sendTo(player, new AnalyzedItemsMessage(true, List.of()));
    }

    @Override
    public void resetAnalyzedPlayers(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_PLAYERS);
        Balm.getNetworking().sendTo(player, new AnalyzedPlayersMessage(true, List.of()));
    }

    @Override
    public void resetAnalyzedEntities(Player player) {
        final var data = getPersistentData(player);
        data.remove(ANALYZED_ENTITIES);
        Balm.getNetworking().sendTo(player, new AnalyzedEntitiesMessage(true, List.of()));
    }

    @Override
    public int getDataCollected(Player player) {
        final var data = getPersistentData(player);
        return data.getInt("PersonalDataCollected");
    }

    public boolean isDataMinedEventDownloaded(Player player, DataMinedEvent event) {
        final var data = getPersistentData(player);
        final var downloaded = data.getCompound(DOWNLOADED_EVENTS);
        return downloaded.getBoolean(event.asKey());
    }

    public void downloadDataMinedEvent(Player player, DataMinedEvent event) {
        final var data = getPersistentData(player);
        final var downloaded = data.getCompound(DOWNLOADED_EVENTS);
        downloaded.putBoolean(event.asKey(), true);
        data.put(DOWNLOADED_EVENTS, downloaded);

        grantData(player, event.dataMined());
    }

    public void resetDataMinedEvents(Player player) {
        final var data = getPersistentData(player);
        data.remove(DOWNLOADED_EVENTS);
    }
}
