package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.menu.AbstractNonogramMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record NonogramAutoHackResultMessage(int containerId, int column, int row, int mark) implements CustomPacketPayload {
    public static final Type<NonogramAutoHackResultMessage> TYPE = new Type<>(id("nonogram_auto_hack_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NonogramAutoHackResultMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            NonogramAutoHackResultMessage::containerId,
            ByteBufCodecs.VAR_INT,
            NonogramAutoHackResultMessage::column,
            ByteBufCodecs.VAR_INT,
            NonogramAutoHackResultMessage::row,
            ByteBufCodecs.BYTE.map(Byte::intValue, Integer::byteValue),
            NonogramAutoHackResultMessage::mark,
            NonogramAutoHackResultMessage::new
    );

    @Override
    public Type<NonogramAutoHackResultMessage> type() {
        return TYPE;
    }

    public static void handle(Player player, NonogramAutoHackResultMessage message) {
        if (player.containerMenu.containerId == message.containerId
                && player.containerMenu instanceof AbstractNonogramMenu menu) {
            player.playSound(SoundEvents.AMETHYST_BLOCK_PLACE, 1f, (float) (0.8f + Math.random() * 0.4f));
            menu.mark(message.column, message.row, message.mark);
        }
    }
}
