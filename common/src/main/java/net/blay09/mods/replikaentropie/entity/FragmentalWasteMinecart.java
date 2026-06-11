package net.blay09.mods.replikaentropie.entity;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWasteLogic;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class FragmentalWasteMinecart extends AbstractMinecart {
    private final FragmentalWasteLogic logic = new FragmentalWasteLogic();

    public FragmentalWasteMinecart(EntityType<? extends FragmentalWasteMinecart> entityType, Level level) {
        super(entityType, level);
    }

    public boolean decontaminationTick() {
        return logic.decontaminationTick(level(), new Vec3(getX(), getY() + 0.5, getZ()), blockPosition());
    }

    public void convertToWasteBarrel() {
        if (level() instanceof ServerLevel serverLevel) {
            logic.playConversionEffects(serverLevel, new Vec3(getX(), getY() + 1.25, getZ()), blockPosition());

            final var wasteBarrelMinecart = new WasteBarrelMinecart(ModEntities.wasteBarrelMinecart.value(), serverLevel);
            wasteBarrelMinecart.setPos(getX(), getY(), getZ());
            wasteBarrelMinecart.setYRot(getYRot());
            wasteBarrelMinecart.setXRot(getXRot());
            wasteBarrelMinecart.setDeltaMovement(getDeltaMovement());
            serverLevel.addFreshEntity(wasteBarrelMinecart);
            discard();
        }
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

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        logic.save(output);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        logic.load(input);
    }
}
