package net.blay09.mods.replikaentropie.core.crane;

import net.blay09.mods.replikaentropie.api.crane.CraneTransfer;
import net.blay09.mods.replikaentropie.api.crane.CraneHandlerRegistry;
import net.blay09.mods.replikaentropie.api.crane.CranePickupHandler;
import net.blay09.mods.replikaentropie.api.crane.CranePlacementHandler;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.entity.ModEntities;
import net.blay09.mods.replikaentropie.entity.WasteBarrelMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class MinecartCraneHandlers {

    private static final double MAX_DISTANCE_FROM_SOURCE_CENTER = 0.25;

    private MinecartCraneHandlers() {
    }

    public static void initialize() {
        registerPickup(MinecartHopper.class);
        registerPickup(MinecartFurnace.class);
        registerPickup(MinecartChest.class);
        registerPickup(MinecartTNT.class);
        registerPickup(WasteBarrelMinecart.class);
        CraneHandlerRegistry.registerPlacement(new MinecartPlacementHandler());
    }

    private static <T extends AbstractMinecart> void registerPickup(Class<T> minecartClass) {
        CraneHandlerRegistry.registerPickup(new MinecartPickupHandler<>(minecartClass));
    }

    private static class MinecartPickupHandler<T extends AbstractMinecart> implements CranePickupHandler {
        private final Class<T> minecartClass;

        private MinecartPickupHandler(Class<T> minecartClass) {
            this.minecartClass = minecartClass;
        }

        @Override
        public @Nullable CraneTransfer peekPickup(ServerLevel level, BlockPos sourcePos) {
            for (final var minecart : level.getEntitiesOfClass(minecartClass, new AABB(sourcePos))) {
                final var transfer = peekPickup(level, sourcePos, minecart);
                if (transfer != null) {
                    return transfer;
                }
            }
            return null;
        }

        private @Nullable CraneTransfer peekPickup(ServerLevel level, BlockPos sourcePos, T minecart) {
            if (!isCenteredAt(minecart, sourcePos)) {
                return null;
            }

            final var carriedState = minecart.getDefaultDisplayBlockState();
            final var blockEntityData = createBlockEntityData(level, sourcePos, minecart, carriedState);
            return new CraneTransfer(carriedState, blockEntityData);
        }

        @Override
        public @Nullable CraneTransfer tryPickup(ServerLevel level, BlockPos sourcePos) {
            for (final var minecart : level.getEntitiesOfClass(minecartClass, new AABB(sourcePos))) {
                final var transfer = peekPickup(level, sourcePos, minecart);
                if (transfer == null) {
                    continue;
                }

                final var replacement = new Minecart(EntityType.MINECART, level);
                replacement.setInitialPos(minecart.getX(), minecart.getY(), minecart.getZ());
                replacement.setYRot(minecart.getYRot());
                replacement.setXRot(minecart.getXRot());
                replacement.setDeltaMovement(minecart.getDeltaMovement());
                if (!level.addFreshEntity(replacement)) {
                    continue;
                }

                minecart.discard();
                return transfer;
            }
            return null;
        }
    }

    private static class MinecartPlacementHandler implements CranePlacementHandler {
        @Override
        public boolean canPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
            for (final var minecart : level.getEntitiesOfClass(Minecart.class, new AABB(destinationPos))) {
                if (canPlace(level, destinationPos, minecart, transfer)) {
                    return true;
                }
            }
            return false;
        }

        private boolean canPlace(ServerLevel level, BlockPos destinationPos, Minecart minecart, CraneTransfer transfer) {
            return minecart.getType() == EntityType.MINECART
                    && isCenteredAt(minecart, destinationPos)
                    && createMinecart(level, transfer.state()) != null;
        }

        @Override
        public boolean tryPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
            for (final var minecart : level.getEntitiesOfClass(Minecart.class, new AABB(destinationPos))) {
                if (!canPlace(level, destinationPos, minecart, transfer)) {
                    continue;
                }

                final var replacement = createMinecart(level, transfer.state());
                if (replacement == null) {
                    continue;
                }

                replacement.setInitialPos(minecart.getX(), minecart.getY(), minecart.getZ());
                replacement.setYRot(minecart.getYRot());
                replacement.setXRot(minecart.getXRot());
                replacement.setDeltaMovement(minecart.getDeltaMovement());
                restoreContainerData(level, destinationPos, transfer, replacement);
                if (!level.addFreshEntity(replacement)) {
                    continue;
                }

                minecart.discard();
                return true;
            }
            return false;
        }
    }

    private static @Nullable AbstractMinecart createMinecart(ServerLevel level, BlockState state) {
        if (state.is(Blocks.HOPPER)) {
            return new MinecartHopper(EntityType.HOPPER_MINECART, level);
        } else if (state.is(Blocks.FURNACE)) {
            return new MinecartFurnace(EntityType.FURNACE_MINECART, level);
        } else if (state.is(Blocks.CHEST)) {
            return new MinecartChest(EntityType.CHEST_MINECART, level);
        } else if (state.is(Blocks.TNT)) {
            return new MinecartTNT(EntityType.TNT_MINECART, level);
        } else if (state.is(ModBlocks.wasteBarrel)) {
            return new WasteBarrelMinecart(ModEntities.wasteBarrelMinecart.value(), level);
        }
        return null;
    }

    private static void restoreContainerData(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer, AbstractMinecart minecart) {
        if (!(minecart instanceof Container destinationContainer) || transfer.blockEntityData() == null) {
            return;
        }

        final var blockEntity = BlockEntity.loadStatic(destinationPos, transfer.state(), transfer.blockEntityData(), level.registryAccess());
        if (!(blockEntity instanceof Container sourceContainer)) {
            return;
        }

        copyContainer(sourceContainer, destinationContainer);
    }

    private static @Nullable CompoundTag createBlockEntityData(ServerLevel level, BlockPos sourcePos, AbstractMinecart minecart, BlockState carriedState) {
        if (!(minecart instanceof Container sourceContainer) || !(carriedState.getBlock() instanceof EntityBlock entityBlock)) {
            return null;
        }

        final var blockEntity = entityBlock.newBlockEntity(sourcePos, carriedState);
        if (!(blockEntity instanceof Container destinationContainer)) {
            return null;
        }

        copyContainer(sourceContainer, destinationContainer);
        return blockEntity.saveWithFullMetadata(level.registryAccess());
    }

    private static void copyContainer(Container sourceContainer, Container destinationContainer) {
        final var size = Math.min(sourceContainer.getContainerSize(), destinationContainer.getContainerSize());
        for (int i = 0; i < size; i++) {
            destinationContainer.setItem(i, sourceContainer.getItem(i).copy());
        }
    }

    private static boolean isCenteredAt(AbstractMinecart minecart, BlockPos pos) {
        final var offsetX = minecart.getX() - (pos.getX() + 0.5);
        final var offsetZ = minecart.getZ() - (pos.getZ() + 0.5);
        return offsetX * offsetX + offsetZ * offsetZ <= MAX_DISTANCE_FROM_SOURCE_CENTER * MAX_DISTANCE_FROM_SOURCE_CENTER;
    }
}
