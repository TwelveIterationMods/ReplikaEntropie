package net.blay09.mods.replikaentropie.effect;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModEffects {

    public static Holder<MobEffect> entropicSpeed;
    public static Holder<MobEffect> fragmentalContamination;

    public static void initialize(BalmRegistrar.Scoped<MobEffect> registries) {
        entropicSpeed = registries.register("entropic_speed", _ ->
                new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xFF33EBFF)
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("entropic_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)).asHolder();
        fragmentalContamination = registries.register("fragmental_contamination", _ -> new FragmentalContaminationEffect()).asHolder();
    }
}
