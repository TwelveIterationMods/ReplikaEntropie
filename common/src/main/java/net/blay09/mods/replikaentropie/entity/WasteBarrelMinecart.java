package net.blay09.mods.replikaentropie.entity;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WasteBarrelMinecart extends AbstractMinecart {

    public WasteBarrelMinecart(EntityType<? extends WasteBarrelMinecart> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Item getDropItem() {
        return ModItems.wasteBarrelMinecart.asItem();
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return ModBlocks.wasteBarrel.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.wasteBarrelMinecart.asItem());
    }
}
