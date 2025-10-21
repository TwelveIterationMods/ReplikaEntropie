package net.blay09.mods.replikaentropie.mixin;

import net.blay09.mods.replikaentropie.item.BurstDrillItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "destroyBlock", at = @At("HEAD"))
    public void destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BurstDrillItem.onClientDestroyBlock(Minecraft.getInstance().player, pos);
    }
}
