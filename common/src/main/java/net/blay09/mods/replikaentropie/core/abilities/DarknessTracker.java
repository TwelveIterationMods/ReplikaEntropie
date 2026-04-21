package net.blay09.mods.replikaentropie.core.abilities;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class DarknessTracker {

    private static final String TAG_KEY = "DarknessTicks";
    private static final int THRESHOLD_TICKS = 30;
    private static final int DARK_LIGHT_LEVEL_MAX = 4;

    public static void tick(ServerPlayer player) {
        final var light = player.level().getMaxLocalRawBrightness(player.blockPosition());
        if (light > DARK_LIGHT_LEVEL_MAX) {
            set(player, Mth.clamp(get(player) - 1, 0, THRESHOLD_TICKS));
        } else {
            set(player, Mth.clamp(get(player) + 1, 0, THRESHOLD_TICKS));
        }
    }

    public static boolean isInTheDark(ServerPlayer player) {
        return get(player) >= THRESHOLD_TICKS;
    }

    public static boolean isWithTheLight(ServerPlayer player) {
        return get(player) <= 0;
    }

    public static void reset(ServerPlayer player) {
        set(player, 0);
    }

    public static int get(ServerPlayer player) {
        final var data = Balm.hooks().getPersistentData(player);
        final var modData = data.getCompoundOrEmpty(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        return modData.getIntOr(TAG_KEY, 0);
    }

    public static void set(ServerPlayer player, int value) {
        final var data = Balm.hooks().getPersistentData(player);
        final var modData = data.getCompoundOrEmpty(ReplikaEntropie.MOD_ID);
        if (modData.isEmpty()) {
            data.put(ReplikaEntropie.MOD_ID, modData);
        }
        modData.putInt(TAG_KEY, Mth.clamp(value, 0, THRESHOLD_TICKS));
    }
}
