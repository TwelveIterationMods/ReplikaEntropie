package net.blay09.mods.replikaentropie.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.replikaentropie.network.protocol.*;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerClientboundPacket(AnalyzedItemsMessage.TYPE, AnalyzedItemsMessage.class, AnalyzedItemsMessage.STREAM_CODEC, AnalyzedItemsMessage::handle);
        networking.registerClientboundPacket(AnalyzedPlayersMessage.TYPE, AnalyzedPlayersMessage.class, AnalyzedPlayersMessage.STREAM_CODEC, AnalyzedPlayersMessage::handle);
        networking.registerClientboundPacket(AnalyzedEntitiesMessage.TYPE, AnalyzedEntitiesMessage.class, AnalyzedEntitiesMessage.STREAM_CODEC, AnalyzedEntitiesMessage::handle);
        networking.registerServerboundPacket(AnalyzePosMessage.TYPE, AnalyzePosMessage.class, AnalyzePosMessage.STREAM_CODEC, AnalyzePosMessage::handle);
        networking.registerServerboundPacket(AnalyzeEntityMessage.TYPE, AnalyzeEntityMessage.class, AnalyzeEntityMessage.STREAM_CODEC, AnalyzeEntityMessage::handle);
        networking.registerClientboundPacket(DataCollectedMessage.TYPE, DataCollectedMessage.class, DataCollectedMessage.STREAM_CODEC, DataCollectedMessage::handle);
        networking.registerClientboundPacket(BurstEnergyMessage.TYPE, BurstEnergyMessage.class, BurstEnergyMessage.STREAM_CODEC, BurstEnergyMessage::handle);
        networking.registerClientboundPacket(AbilityStateMessage.TYPE, AbilityStateMessage.class, AbilityStateMessage.STREAM_CODEC, AbilityStateMessage::handle);
        networking.registerServerboundPacket(ToggleAbilityMessage.TYPE, ToggleAbilityMessage.class, ToggleAbilityMessage.STREAM_CODEC, ToggleAbilityMessage::handle);
        networking.registerClientboundPacket(NonogramAutoHackResultMessage.TYPE, NonogramAutoHackResultMessage.class, NonogramAutoHackResultMessage.STREAM_CODEC, NonogramAutoHackResultMessage::handle);
        networking.registerServerboundPacket(NonogramAutoHackMessage.TYPE, NonogramAutoHackMessage.class, NonogramAutoHackMessage.STREAM_CODEC, NonogramAutoHackMessage::handle);
        networking.registerServerboundPacket(NonogramMarkMessage.TYPE, NonogramMarkMessage.class, NonogramMarkMessage.STREAM_CODEC, NonogramMarkMessage::handle);
        networking.registerServerboundPacket(MakeshiftPsuMessage.TYPE, MakeshiftPsuMessage.class, MakeshiftPsuMessage.STREAM_CODEC, MakeshiftPsuMessage::handle);
    }
}
