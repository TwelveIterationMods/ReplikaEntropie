package net.blay09.mods.replikaentropie.core.analyzer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InMemoryAnalyzerManager implements AnalyzerManager {

    private final Set<Item> analyzedItems = new HashSet<>();
    private final Set<UUID> analyzedPlayers = new HashSet<>();
    private final Set<EntityType<?>> analyzedEntityTypes = new HashSet<>();

    @Override
    public void analyzeItem(Player player, ItemStack itemStack) {
        analyzedItems.add(itemStack.getItem());
    }

    @Override
    public boolean isAnalyzed(Player player, ItemStack itemStack) {
        return analyzedItems.contains(itemStack.getItem());
    }

    @Override
    public void analyzeEntity(Player player, Entity entity) {
        if (entity instanceof Player targetPlayer) {
            analyzedPlayers.add(targetPlayer.getUUID());
        } else {
            final var entityType = entity.getType();
            analyzedEntityTypes.add(entityType);
        }
    }

    @Override
    public void grantData(Player player, int amount) {
    }

    @Override
    public boolean isAnalyzed(Player player, Entity entity) {
        if (entity instanceof Player targetPlayer) {
            return analyzedPlayers.contains(targetPlayer.getUUID());
        } else {
            final var entityType = entity.getType();
            return analyzedEntityTypes.contains(entityType);
        }
    }

    @Override
    public void resetAll(Player player) {
        analyzedItems.clear();
        analyzedPlayers.clear();
        analyzedEntityTypes.clear();
    }

    @Override
    public void resetAnalyzedItems(Player player) {
        analyzedItems.clear();
    }

    @Override
    public void resetAnalyzedPlayers(Player player) {
        analyzedPlayers.clear();
    }

    @Override
    public void resetAnalyzedEntities(Player player) {
        analyzedEntityTypes.clear();
    }

    @Override
    public int getDataCollected(Player player) {
        return 0;
    }

    public void analyzePlayer(Player player, UUID targetPlayerUuid) {
        analyzedPlayers.add(targetPlayerUuid);
    }

    public void analyzeEntity(Player player, EntityType<?> entityType) {
        analyzedEntityTypes.add(entityType);
    }
}
