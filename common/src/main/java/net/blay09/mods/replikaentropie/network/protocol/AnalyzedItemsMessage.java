package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record AnalyzedItemsMessage(boolean reset, List<Item> items) implements CustomPacketPayload {
    public static final Type<AnalyzedItemsMessage> TYPE = new Type<>(id("analyzed_items"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AnalyzedItemsMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            AnalyzedItemsMessage::reset,
            ByteBufCodecs.idMapper(BuiltInRegistries.ITEM).apply(ByteBufCodecs.list()),
            AnalyzedItemsMessage::items,
            AnalyzedItemsMessage::new
    );

    @Override
    public Type<AnalyzedItemsMessage> type() {
        return TYPE;
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
