package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record AnalyzedItemsMessage(boolean reset, List<Item> items) {

    public static void encode(AnalyzedItemsMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.reset);
        buf.writeCollection(message.items, (it, item) -> it.writeId(BuiltInRegistries.ITEM, item));
    }

    public static AnalyzedItemsMessage decode(FriendlyByteBuf buf) {
        final var reset = buf.readBoolean();
        final var items = buf.readList((it) -> it.readById(BuiltInRegistries.ITEM));
        return new AnalyzedItemsMessage(reset, items);
    }

    public static void handle(Player player, AnalyzedItemsMessage message) {
        if (message.reset) {
            Analyzer.resetAnalyzedItems(player);
        }
        for (final var item : message.items) {
            Analyzer.analyzeItem(player, new ItemStack(item));
        }
        if (!message.reset && !message.items.isEmpty()) {
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }
    }

}
