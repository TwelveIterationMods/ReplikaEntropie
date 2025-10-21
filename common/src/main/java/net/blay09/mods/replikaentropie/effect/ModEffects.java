package net.blay09.mods.replikaentropie.effect;

import net.blay09.mods.balm.api.BalmRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModEffects {

    public static MobEffect entropicSpeed;
    public static MobEffect fragmentalContamination;

    public static void initialize(BalmRegistries registries) {
        registries.register(BuiltInRegistries.MOB_EFFECT, (identifier) ->
                        entropicSpeed = new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xFF33EBFF)
                                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "a582e4e1-edf5-4c58-b336-478f5c811d9d", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL),
                id("entropic_speed"));
        registries.register(BuiltInRegistries.MOB_EFFECT, (identifier) ->
                        fragmentalContamination = new FragmentalContaminationEffect(),
                id("fragmental_contamination"));
    }
}
