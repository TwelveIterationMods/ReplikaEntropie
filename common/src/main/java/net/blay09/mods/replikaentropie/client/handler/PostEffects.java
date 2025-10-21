package net.blay09.mods.replikaentropie.client.handler;

import net.blay09.mods.replikaentropie.core.abilities.AbilityManager;
import net.blay09.mods.replikaentropie.core.abilities.NightVisionAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PostEffects {
    @Nullable
    private static ResourceLocation currentPostEffect;

    @Nullable
    public static ResourceLocation getEntityPostEffect(@Nullable Entity entity) {
        return currentPostEffect;
    }

    public static void updatePostEffect() {
        final var previousPostEffect = currentPostEffect;
        final var client = Minecraft.getInstance();
        currentPostEffect = client.player != null ? computePostEffect(client.player) : null;
        if (!Objects.equals(previousPostEffect, currentPostEffect)) {
            client.gameRenderer.checkEntityPostEffect(client.options.getCameraType().isFirstPerson() ? client.getCameraEntity() : null);
        }
    }

    @Nullable
    private static ResourceLocation computePostEffect(Player player) {
        if (AbilityManager.isAbilityActive(player, NightVisionAbility.INSTANCE)) {
            return NightVisionAbility.SHADER;
        }

        return null;
    }
}
