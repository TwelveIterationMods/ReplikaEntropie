package net.blay09.mods.replikaentropie.api.crane;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface CranePlacementHandler {

    boolean canPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer);

    boolean tryPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer);
}
