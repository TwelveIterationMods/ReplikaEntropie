package net.blay09.mods.replikaentropie.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.replikaentropie.network.protocol.*;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerClientboundPacket(id("analyzed_items"), AnalyzedItemsMessage.class, AnalyzedItemsMessage::encode, AnalyzedItemsMessage::decode, AnalyzedItemsMessage::handle);
        networking.registerClientboundPacket(id("analyzed_players"), AnalyzedPlayersMessage.class, AnalyzedPlayersMessage::encode, AnalyzedPlayersMessage::decode, AnalyzedPlayersMessage::handle);
        networking.registerClientboundPacket(id("analyzed_entities"), AnalyzedEntitiesMessage.class, AnalyzedEntitiesMessage::encode, AnalyzedEntitiesMessage::decode, AnalyzedEntitiesMessage::handle);
        networking.registerServerboundPacket(id("analyze_pos"), AnalyzePosMessage.class, AnalyzePosMessage::encode, AnalyzePosMessage::decode, AnalyzePosMessage::handle);
        networking.registerServerboundPacket(id("analyze_entity"), AnalyzeEntityMessage.class, AnalyzeEntityMessage::encode, AnalyzeEntityMessage::decode, AnalyzeEntityMessage::handle);
        networking.registerClientboundPacket(id("data_collected"), DataCollectedMessage.class, DataCollectedMessage::encode, DataCollectedMessage::decode, DataCollectedMessage::handle);
        networking.registerClientboundPacket(id("burst_energy"), BurstEnergyMessage.class, BurstEnergyMessage::encode, BurstEnergyMessage::decode, BurstEnergyMessage::handle);
        networking.registerClientboundPacket(id("ability_state"), AbilityStateMessage.class, AbilityStateMessage::encode, AbilityStateMessage::decode, AbilityStateMessage::handle);
        networking.registerServerboundPacket(id("toggle_ability"), ToggleAbilityMessage.class, ToggleAbilityMessage::encode, ToggleAbilityMessage::decode, ToggleAbilityMessage::handle);
        networking.registerServerboundPacket(id("nonogram_mark"), NonogramMarkMessage.class, NonogramMarkMessage::encode, NonogramMarkMessage::decode, NonogramMarkMessage::handle);
    }
}
