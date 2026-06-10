package net.blay09.mods.replikaentropie.api.crane;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

public interface CranePickupHandler {

    @Nullable CraneTransfer peekPickup(ServerLevel level, BlockPos sourcePos);

    @Nullable CraneTransfer tryPickup(ServerLevel level, BlockPos sourcePos);
}
