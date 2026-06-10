package net.blay09.mods.replikaentropie.api.crane;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraneHandlerRegistry {

    private static final List<CranePickupHandler> pickupHandlers = new ArrayList<>();
    private static final List<CranePlacementHandler> placementHandlers = new ArrayList<>();

    private CraneHandlerRegistry() {
    }

    public static void registerPickup(CranePickupHandler handler) {
        pickupHandlers.add(handler);
    }

    public static void registerPlacement(CranePlacementHandler handler) {
        placementHandlers.add(handler);
    }

    public static Optional<CraneTransfer> tryPickup(ServerLevel level, BlockPos sourcePos, BlockPos destinationPos) {
        for (final var handler : pickupHandlers) {
            final var preview = handler.peekPickup(level, sourcePos);
            if (preview == null || !canPlace(level, destinationPos, preview)) {
                continue;
            }

            final var transfer = handler.tryPickup(level, sourcePos);
            if (transfer != null) {
                return Optional.of(transfer);
            }
        }
        return Optional.empty();
    }

    public static boolean canPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
        for (final var handler : placementHandlers) {
            if (handler.canPlace(level, destinationPos, transfer)) {
                return true;
            }
        }
        return false;
    }

    public static boolean tryPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
        for (final var handler : placementHandlers) {
            if (handler.tryPlace(level, destinationPos, transfer)) {
                return true;
            }
        }
        return false;
    }
}
