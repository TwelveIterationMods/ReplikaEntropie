package net.blay09.mods.replikaentropie.mixin;

import net.blay09.mods.replikaentropie.client.handler.PostEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "checkEntityPostEffect(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"), cancellable = true)
    public void checkEntityPostEffect(@Nullable Entity entity, CallbackInfo ci) {
        var effect = PostEffects.getEntityPostEffect(entity);
        if (effect != null) {
            ((GameRendererAccessor) this).callLoadEffect(effect);
            ci.cancel();
        }
    }
}
