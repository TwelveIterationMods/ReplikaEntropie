package net.blay09.mods.replikaentropie.entity;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FragmentalWasteMinecart extends AbstractMinecart {

    public FragmentalWasteMinecart(EntityType<? extends FragmentalWasteMinecart> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount % 20 == 0) {
            FragmentalWaste.applyWasteAroundEntity(this);
        }
    }

    @Override
    protected Item getDropItem() {
        return ModItems.fragmentalWasteMinecart.asItem();
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return ModBlocks.fragmentalWaste.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.fragmentalWasteMinecart.asItem());
    }
}
