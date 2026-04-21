package net.blay09.mods.replikaentropie.effect;

import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FragmentalContaminationEffect extends MobEffect {
    public FragmentalContaminationEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF9B59B6);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
        mob.hurtServer(serverLevel, FragmentalWaste.damageSource(serverLevel), (amplification + 1) * 0.5f);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        final var mod = 40 >> amplifier;
        return mod == 0 || duration % mod == 0;
    }

    @Override
    public int getColor() {
        return 0xFFFF0000;
    }
}
