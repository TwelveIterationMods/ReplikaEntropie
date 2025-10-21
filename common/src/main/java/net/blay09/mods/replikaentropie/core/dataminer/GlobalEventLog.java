package net.blay09.mods.replikaentropie.core.dataminer;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.LivingDeathEvent;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.PlayerLogoutEvent;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GlobalEventLog extends SavedData {

    private static final String DATA_NAME = ReplikaEntropie.MOD_ID + "_GlobalEventLog";
    private static final String EVENTS = "Events";

    private final List<DataMinedEvent> events = new ArrayList<>();

    public static void initialize(BalmEvents events) {
        events.onEvent(LivingDeathEvent.class, event -> {
            final var entity = event.getEntity();
            final var showDeathMessages = entity.getServer().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
            if (showDeathMessages && entity instanceof Player) {
                GlobalEventLog.get(entity.getServer())
                        .ifPresent(it -> {
                            final var icon = DataMinedEvent.createEntityIcon(entity);
                            final var variant = DataMinedEvent.getEntityVariant(entity) + "_" + event.getDamageSource().getMsgId();
                            final var deathMessage = event.getDamageSource().getLocalizedDeathMessage(entity);
                            it.events.add(DataMinedEvent.of(DataMinedEvent.Type.PLAYER_DIED, variant, icon, deathMessage));
                        });
            }
        });

        events.onEvent(PlayerLoginEvent.class, event -> {
            final var icon = new ItemStack(Items.PLAYER_HEAD);
            final var name = event.getPlayer().getGameProfile().getName();
            icon.getOrCreateTag().putString(PlayerHeadItem.TAG_SKULL_OWNER, name);
            GlobalEventLog.get(event.getPlayer().getServer())
                    .ifPresent(it -> it.events.add(DataMinedEvent.of(DataMinedEvent.Type.PLAYER_JOINED, name, icon, event.getPlayer().getName())));
        });

        events.onEvent(PlayerLogoutEvent.class, event -> {
            final var icon = new ItemStack(Items.PLAYER_HEAD);
            final var name = event.getPlayer().getGameProfile().getName();
            icon.getOrCreateTag().putString(PlayerHeadItem.TAG_SKULL_OWNER, name);
            GlobalEventLog.get(event.getPlayer().getServer())
                    .ifPresent(it -> it.events.add(DataMinedEvent.of(DataMinedEvent.Type.PLAYER_LEFT, name, icon, event.getPlayer().getName())));
        });
    }

    public static GlobalEventLog load(CompoundTag compound) {
        final var globalEventLog = new GlobalEventLog();
        final var tagList = compound.getList(EVENTS, Tag.TAG_COMPOUND);
        for (final var tag : tagList) {
            if (tag instanceof CompoundTag compoundTag) {
                globalEventLog.events.add(DataMinedEvent.of(compoundTag));
            }
        }
        return globalEventLog;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        final var tagList = new ListTag();
        for (final var event : events) {
            tagList.add(event.save(new CompoundTag()));
        }
        compoundTag.put(EVENTS, tagList);
        return compoundTag;
    }

    public static Optional<GlobalEventLog> get(@Nullable MinecraftServer server) {
        return Optional.ofNullable(server)
                .map(it -> it.getLevel(Level.OVERWORLD))
                .map(it -> it.getDataStorage().computeIfAbsent(GlobalEventLog::load, GlobalEventLog::new, DATA_NAME));
    }

}
