package net.blay09.mods.replikaentropie.entity;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.FragmentalWasteBlockEntity;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FragmentalWasteMinecart extends AbstractMinecart {
    private int decontaminationTicks;

    public FragmentalWasteMinecart(EntityType<? extends FragmentalWasteMinecart> entityType, Level level) {
        super(entityType, level);
    }

    public boolean decontaminationTick() {
        if (decontaminationTicks < FragmentalWasteBlockEntity.DECONTAMINATION_TICKS_REQUIRED) {
            decontaminationTicks++;
            if (level() instanceof ServerLevel serverLevel) {
                if (decontaminationTicks % 20 == 0) {
                    serverLevel.sendParticles(ParticleTypes.DUST_PLUME, getX(), getY() + 0.5, getZ(), 4, 0.75f, 0.75f, 0.75f, 0);
                }
                if (decontaminationTicks % 40 == 0) {
                    serverLevel.playSound(null, blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1f, (float) (0.5 + Math.random()));
                }
            }
        }
        return decontaminationTicks >= FragmentalWasteBlockEntity.DECONTAMINATION_TICKS_REQUIRED;
    }

    public void convertToWasteBarrel() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, getX(), getY() + 1.25, getZ(), 4, 0.25f, 0.25f, 0.25f, 0);
            serverLevel.playSound(null, blockPosition(), SoundEvents.ALLAY_DEATH, SoundSource.BLOCKS, 1f, (float) (0.5 + Math.random()));

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
        output.putInt("DecontaminationTicks", decontaminationTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        decontaminationTicks = input.getIntOr("DecontaminationTicks", 0);
    }
}
