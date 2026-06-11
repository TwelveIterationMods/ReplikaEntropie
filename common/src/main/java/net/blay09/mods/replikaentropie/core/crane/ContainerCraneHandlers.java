package net.blay09.mods.replikaentropie.core.crane;

import net.blay09.mods.replikaentropie.api.crane.CraneHandlerRegistry;
import net.blay09.mods.replikaentropie.api.crane.CranePlacementHandler;
import net.blay09.mods.replikaentropie.api.crane.CraneTransfer;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class ContainerCraneHandlers {

    private ContainerCraneHandlers() {
    }

    public static void initialize() {
        CraneHandlerRegistry.registerPlacement(new ContainerCranePlacementHandler());
    }

    private static class ContainerCranePlacementHandler implements CranePlacementHandler {
        @Override
        public boolean canPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
            if (!canStore(transfer)) {
                return false;
            }

            final var container = getDestinationContainer(level, destinationPos);
            final var itemStack = createItemStack(level, destinationPos, transfer);
            return container != null && canInsert(container, itemStack);
        }

        @Override
        public boolean tryPlace(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
            if (!canStore(transfer)) {
                return false;
            }

            final var container = getDestinationContainer(level, destinationPos);
            if (container == null) {
                return false;
            }

            final var itemStack = createItemStack(level, destinationPos, transfer);
            return canInsert(container, itemStack)
                    && HopperBlockEntity.addItem(null, container, itemStack, null).isEmpty();
        }
    }

    private static boolean canStore(CraneTransfer transfer) {
        return transfer.state().is(Blocks.TNT)
                || transfer.state().is(ModBlocks.wasteBarrel)
                || transfer.state().is(ModBlocks.fragmentalWaste);
    }

    private static @Nullable Container getDestinationContainer(ServerLevel level, BlockPos destinationPos) {
        final var blockEntity = level.getBlockEntity(destinationPos);
        if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof HopperBlockEntity) {
            return HopperBlockEntity.getContainerAt(level, destinationPos);
        }

        for (final var minecart : level.getEntitiesOfClass(MinecartChest.class, new AABB(destinationPos))) {
            return minecart;
        }
        for (final var minecart : level.getEntitiesOfClass(MinecartHopper.class, new AABB(destinationPos))) {
            return minecart;
        }
        return null;
    }

    private static ItemStack createItemStack(ServerLevel level, BlockPos destinationPos, CraneTransfer transfer) {
        final var itemStack = new ItemStack(transfer.state().getBlock());
        if (itemStack.isEmpty() || transfer.blockEntityData() == null) {
            return itemStack;
        }

        final var blockEntity = BlockEntity.loadStatic(destinationPos, transfer.state(), transfer.blockEntityData(), level.registryAccess());
        if (blockEntity != null) {
            itemStack.applyComponents(blockEntity.collectComponents());
            itemStack.set(DataComponents.BLOCK_ENTITY_DATA,
                    TypedEntityData.of(blockEntity.getType(), blockEntity.saveCustomOnly(level.registryAccess())));
        }
        return itemStack;
    }

    private static boolean canInsert(Container container, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (!container.canPlaceItem(slot, itemStack)) {
                continue;
            }

            final var existing = container.getItem(slot);
            if (existing.isEmpty()) {
                return container.getMaxStackSize(itemStack) > 0;
            }

            final var maxStackSize = Math.min(container.getMaxStackSize(existing), existing.getMaxStackSize());
            if (ItemStack.isSameItemSameComponents(existing, itemStack) && existing.getCount() < maxStackSize) {
                return true;
            }
        }
        return false;
    }
}
