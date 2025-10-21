package net.blay09.mods.replikaentropie.mixin;

import net.blay09.mods.replikaentropie.core.abilities.JumpBoostAbility;
import net.blay09.mods.replikaentropie.core.dataminer.LocalEventLog;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void jumpFromGround(CallbackInfo ci) {
        final var entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            JumpBoostAbility.onJumpFromGround(player);
            LocalEventLog.onJumpFromGround(player);
        }
    }
}
