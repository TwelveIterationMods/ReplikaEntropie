package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class FragmentalWasteItem extends BlockItem {
    public FragmentalWasteItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        super.onDestroyed(itemEntity);
        itemEntity.level().explode(itemEntity, itemEntity.getX(), itemEntity.getY() + 1, itemEntity.getZ(), 3f, true, Level.ExplosionInteraction.BLOCK);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (owner.tickCount % 20 == 0) {
            FragmentalWaste.applyWasteAroundEntity(owner);
        }
    }
}
