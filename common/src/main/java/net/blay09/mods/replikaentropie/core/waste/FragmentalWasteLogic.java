package net.blay09.mods.replikaentropie.core.waste;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class FragmentalWasteLogic {
    public static final int DECONTAMINATION_TICKS_REQUIRED = 200;

    private static final String DECONTAMINATION_TICKS_TAG = "DecontaminationTicks";

    private final Runnable changedCallback;
    private int decontaminationTicks;

    public FragmentalWasteLogic() {
        this(() -> {
        });
    }

    public FragmentalWasteLogic(Runnable changedCallback) {
        this.changedCallback = changedCallback;
    }

    public int getDecontaminationTicks() {
        return decontaminationTicks;
    }

    public boolean decontaminationTick(Level level, Vec3 effectPosition, BlockPos soundPosition) {
        if (decontaminationTicks < DECONTAMINATION_TICKS_REQUIRED) {
            decontaminationTicks++;
            if (level instanceof ServerLevel serverLevel) {
                if (decontaminationTicks % 20 == 0) {
                    serverLevel.sendParticles(ParticleTypes.DUST_PLUME, effectPosition.x, effectPosition.y, effectPosition.z, 4, 0.75f, 0.75f, 0.75f, 0);
                }
                if (decontaminationTicks % 40 == 0) {
                    serverLevel.playSound(null, soundPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1f, (float) (0.5 + Math.random()));
                }
            }
            changedCallback.run();
        }
        return decontaminationTicks >= DECONTAMINATION_TICKS_REQUIRED;
    }

    public void playConversionEffects(Level level, Vec3 effectPosition, BlockPos soundPosition) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, effectPosition.x, effectPosition.y, effectPosition.z, 4, 0.25f, 0.25f, 0.25f, 0);
            serverLevel.playSound(null, soundPosition, SoundEvents.ALLAY_DEATH, SoundSource.BLOCKS, 1f, (float) (0.5 + Math.random()));
        }
    }

    public void save(ValueOutput output) {
        output.putInt(DECONTAMINATION_TICKS_TAG, decontaminationTicks);
    }

    public void load(ValueInput input) {
        decontaminationTicks = input.getIntOr(DECONTAMINATION_TICKS_TAG, 0);
    }
}
