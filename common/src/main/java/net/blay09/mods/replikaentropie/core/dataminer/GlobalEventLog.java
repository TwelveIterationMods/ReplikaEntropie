package net.blay09.mods.replikaentropie.core.dataminer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class GlobalEventLog extends SavedData {

    private static final String EVENTS = "Events";
    private static final Codec<GlobalEventLog> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DataMinedEvent.CODEC.listOf().optionalFieldOf(EVENTS, List.of()).forGetter(it -> it.events)
    ).apply(instance, GlobalEventLog::new));
    private static final SavedDataType<GlobalEventLog> TYPE = new SavedDataType<>(
            id("global_event_log"),
            GlobalEventLog::new,
            CODEC,
            null);

    private final List<DataMinedEvent> events = new ArrayList<>();

    public GlobalEventLog() {
    }

    private GlobalEventLog(List<DataMinedEvent> events) {
        this.events.addAll(events);
    }

    public static void initialize() {
        LivingEntityCallback.Death.Before.EVENT.register(((entity, damageSource) -> {
            if (entity.level() instanceof ServerLevel serverLevel) {
                final var showDeathMessages = serverLevel.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES);
                if (showDeathMessages && entity instanceof Player) {
                    GlobalEventLog.get(serverLevel.getServer())
                            .ifPresent(it -> {
                                final var icon = DataMinedEvent.createEntityIcon(entity);
                                final var variant = DataMinedEvent.getEntityVariant(entity) + "_" + damageSource.getMsgId();
                                final var deathMessage = damageSource.getLocalizedDeathMessage(entity);
                                it.addEvent(DataMinedEvent.of(DataMinedEvent.Type.PLAYER_DIED, variant, icon, deathMessage));
                            });
                }
            }
            return true;
        }));

        ServerPlayerCallback.Join.EVENT.register(player -> {
            final var icon = DataMinedEvent.createPlayerIcon(player);
            final var name = player.getGameProfile().name();
            GlobalEventLog.get(player.level().getServer())
                    .ifPresent(it -> it.addEvent(DataMinedEvent.of(DataMinedEvent.Type.PLAYER_JOINED, name, icon, player.getName())));
        });

        ServerPlayerCallback.Leave.EVENT.register(player -> {
            final var icon = DataMinedEvent.createPlayerIcon(player);
            final var name = player.getGameProfile().name();
            GlobalEventLog.get(player.level().getServer())
                    .ifPresent(it -> it.addEvent(DataMinedEvent.of(DataMinedEvent.Type.PLAYER_LEFT, name, icon, player.getName())));
        });
    }

    private void addEvent(DataMinedEvent event) {
        events.add(event);
        setDirty();
    }

    public static Optional<GlobalEventLog> get(@Nullable MinecraftServer server) {
        return Optional.ofNullable(server)
                .map(it -> it.getLevel(Level.OVERWORLD))
                .map(it -> it.getDataStorage().computeIfAbsent(TYPE));
    }

}
