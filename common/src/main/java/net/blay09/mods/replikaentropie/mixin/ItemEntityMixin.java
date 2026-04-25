package net.blay09.mods.replikaentropie.mixin;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        final var itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.getItem().is(ModBlocks.fragmentalWaste.asItem())) {
            FragmentalWaste.applyWasteAroundEntity(itemEntity);
        }
    }
}
