package net.blay09.mods.replikaentropie.core.waste;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.PlayerOpenMenuEvent;
import net.blay09.mods.replikaentropie.item.HazmatArmorItem;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.blay09.mods.replikaentropie.effect.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class FragmentalWaste {

    private static final ResourceKey<DamageType> FRAGMENTAL_WASTE_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, id("fragmental_contamination"));
    private static final int CONTAMINATION_DURATION = 100;
    private static final int CONTAMINATION_RENEW_PERIOD = 30;

    public static void initialize(BalmEvents events) {
        events.onEvent(PlayerOpenMenuEvent.class, (event) -> {
            for (final var itemStack : event.getMenu().getItems()) {
                if (itemStack.is(ModItems.fragmentalWaste)) {
                    applyWasteAroundEntity(event.getPlayer());
                    break;
                }
            }

            event.getMenu().addSlotListener(new ContainerListener() {
                @Override
                public void slotChanged(AbstractContainerMenu menu, int slotId, ItemStack itemStack) {
                    if (itemStack.is(ModItems.fragmentalWaste)) {
                        applyWasteAroundEntity(event.getPlayer());
                    }
                }

                @Override
                public void dataChanged(AbstractContainerMenu abstractContainerMenu, int i, int i1) {
                }
            });
        });
    }

    public static void applyWasteAroundEntity(Entity source) {
        final var nearbyEntities = source.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), source.getBoundingBox().inflate(3f), it -> true);
        for (final var entity : nearbyEntities) {
            if (!HazmatArmorItem.tryProtect(entity)) {
                addOrRenewContamination(entity);
            }
        }
    }

    public static void applyWasteAroundBlockEntity(BlockEntity blockEntity) {
        final var level = blockEntity.getLevel();
        final var pos = blockEntity.getBlockPos();
        final var entities = level.getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(pos).inflate(3), e -> true);
        for (final var entity : entities) {
            if (!HazmatArmorItem.tryProtect(entity)) {
                addOrRenewContamination(entity);
            }
        }
    }

    private static void addOrRenewContamination(LivingEntity entity) {
        final var current = entity.getEffect(ModEffects.fragmentalContamination);
        if (current == null) {
            entity.addEffect(new MobEffectInstance(ModEffects.fragmentalContamination, CONTAMINATION_DURATION));
        } else if (current.getDuration() < CONTAMINATION_RENEW_PERIOD) {
            entity.addEffect(new MobEffectInstance(ModEffects.fragmentalContamination, CONTAMINATION_DURATION, current.getAmplifier() + 1));
        }
    }

    public static DamageSource damageSource(Level level) {
        final var damageTypes = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(damageTypes.getHolderOrThrow(FRAGMENTAL_WASTE_DAMAGE_TYPE));
    }
}
