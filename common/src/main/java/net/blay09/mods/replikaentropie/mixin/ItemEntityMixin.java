package net.blay09.mods.replikaentropie.mixin;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.FragmentalWasteBlockEntity;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        final var itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.getItem().is(ModItems.fragmentalWaste)) {
            FragmentalWaste.applyWasteAroundEntity(itemEntity);

            if (itemEntity.getAge() + 1 >= 6000) {
                final var level = itemEntity.level();
                final var pos = itemEntity.blockPosition();
                if (level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, ModBlocks.fragmentalWaste.defaultBlockState(), Block.UPDATE_ALL);
                    if (level.getBlockEntity(pos) instanceof FragmentalWasteBlockEntity fragmentalWasteBlockEntity) {
                        fragmentalWasteBlockEntity.setWasteCount(itemEntity.getItem().getCount());
                    }
                    itemEntity.discard();
                } else {
                    itemEntity.setExtendedLifetime();
                }
            }
        }
    }
}
