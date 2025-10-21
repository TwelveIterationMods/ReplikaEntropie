package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.blay09.mods.replikaentropie.core.dataminer.LocalEventLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChaosEngineBlockEntity extends BalmBlockEntity {

    private static final List<String> GIBBERISH = List.of(
            "gui.replikaentropie.entropic_data_miner.event.chaos.blorfed",
            "gui.replikaentropie.entropic_data_miner.event.chaos.snazzled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.quonked",
            "gui.replikaentropie.entropic_data_miner.event.chaos.flumbled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.dringled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.splooted",
            "gui.replikaentropie.entropic_data_miner.event.chaos.wazzled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.cronked",
            "gui.replikaentropie.entropic_data_miner.event.chaos.zibbled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.glorped",
            "gui.replikaentropie.entropic_data_miner.event.chaos.twibbled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.zumbled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.drabbled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.spraffled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.whabbled",
            "gui.replikaentropie.entropic_data_miner.event.chaos.frozzled"
    );

    private static List<BlockState> BLOCK_CANDIDATES;
    private static List<SoundEvent> SOUND_CANDIDATES;
    private static List<ParticleOptions> PARTICLE_CANDIDATES;

    private int ticksSinceDisplayChange;
    private BlockState displayBlockState;

    private int ticksSinceEventGenerated;
    private int ticksUntilNextSound = -1;
    private int ticksUntilNextParticle = -1;

    public ChaosEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.chaosEngine.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ChaosEngineBlockEntity blockEntity) {
        blockEntity.processDisplayChange();
        blockEntity.processRandomParticles();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChaosEngineBlockEntity blockEntity) {
        blockEntity.processChaos();
        blockEntity.processRandomSounds();
    }

    private void processChaos() {
        ticksSinceEventGenerated++;
        if (ticksSinceEventGenerated >= 100) {
            LocalEventLog.findNearbyDataMiners(level, worldPosition).forEach(dataMiner -> {
                if (dataMiner.countChaosEvents() < 5) {
                    final var variant = UUID.randomUUID().toString();
                    var randomBlock = pickBlockCandidate(level.random);
                    if (randomBlock == null) {
                        randomBlock = Blocks.DIRT.defaultBlockState();
                    }
                    final var gibberish = GIBBERISH.get(level.random.nextInt(GIBBERISH.size()));
                    final var label = Component.translatable(gibberish, new ItemStack(randomBlock.getBlock()).getHoverName());
                    final var event = DataMinedEvent.of(DataMinedEvent.Type.CHAOS, variant, new ItemStack(ModBlocks.chaosEngine), label);
                    dataMiner.addEvent(event);
                }
            });
            ticksSinceEventGenerated = 0;
        }
    }

    private void processDisplayChange() {
        ticksSinceDisplayChange++;
        if (displayBlockState == null || ticksSinceDisplayChange >= 4) {
            displayBlockState = pickBlockCandidate(level.random);
            ticksSinceDisplayChange = 0;
        }
    }

    private BlockState pickBlockCandidate(RandomSource random) {
        if (BLOCK_CANDIDATES == null) {
            BLOCK_CANDIDATES = new ArrayList<>();
            for (final var block : BuiltInRegistries.BLOCK) {
                final var blockId = BuiltInRegistries.BLOCK.getKey(block);
                if (blockId.getNamespace().equals("minecraft")) {
                    final var defaultState = block.defaultBlockState();
                    if (defaultState.getRenderShape() == RenderShape.MODEL) {
                        BLOCK_CANDIDATES.add(defaultState);
                    }
                }
            }
        }

        if (BLOCK_CANDIDATES.isEmpty()) {
            return null;
        }

        return BLOCK_CANDIDATES.get(random.nextInt(BLOCK_CANDIDATES.size()));
    }

    public BlockState getDisplayBlockState() {
        return displayBlockState;
    }

    private void processRandomSounds() {
        if (ticksUntilNextSound > 0) {
            ticksUntilNextSound--;
            return;
        }

        final var sound = pickRandomSound(level.random);
        if (sound != null) {
            float pitch = 0.75f + level.random.nextFloat() * 0.75f;
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.8f, pitch);
        }

        ticksUntilNextSound = level.random.nextInt(40, 200);
    }

    private SoundEvent pickRandomSound(RandomSource random) {
        if (SOUND_CANDIDATES == null) {
            SOUND_CANDIDATES = new ArrayList<>();
            for (final var soundEvent : BuiltInRegistries.SOUND_EVENT) {
                final var soundId = BuiltInRegistries.SOUND_EVENT.getKey(soundEvent);
                if (soundId != null
                        && soundId.getNamespace().equals("minecraft")
                        && !soundId.getPath().startsWith("music.")) {
                    SOUND_CANDIDATES.add(soundEvent);
                }
            }
        }

        if (SOUND_CANDIDATES.isEmpty()) {
            return null;
        }

        return SOUND_CANDIDATES.get(random.nextInt(SOUND_CANDIDATES.size()));
    }

    private void processRandomParticles() {
        if (ticksUntilNextParticle > 0) {
            ticksUntilNextParticle--;
            return;
        }

        ensureParticleCandidates();
        if (PARTICLE_CANDIDATES.isEmpty()) {
            ticksUntilNextParticle = 20;
            return;
        }

        final var rnd = level.random;
        final ParticleOptions particle = PARTICLE_CANDIDATES.get(rnd.nextInt(PARTICLE_CANDIDATES.size()));

        final double baseX = worldPosition.getX() + 0.5;
        final double baseY = worldPosition.getY() + 0.75;
        final double baseZ = worldPosition.getZ() + 0.5;

        int count = rnd.nextInt(3, 8);
        for (int i = 0; i < count; i++) {
            double ox = (rnd.nextDouble() - 0.5) * 0.8;
            double oy = (rnd.nextDouble() - 0.3) * 0.6;
            double oz = (rnd.nextDouble() - 0.5) * 0.8;

            double vx = (rnd.nextDouble() - 0.5) * 0.02;
            double vy = rnd.nextDouble() * 0.04;
            double vz = (rnd.nextDouble() - 0.5) * 0.02;

            level.addParticle(particle, baseX + ox, baseY + oy, baseZ + oz, vx, vy, vz);
        }

        ticksUntilNextParticle = rnd.nextInt(8, 40);
    }

    private static void ensureParticleCandidates() {
        if (PARTICLE_CANDIDATES != null) {
            return;
        }
        PARTICLE_CANDIDATES = new ArrayList<>();
        // Only include simple, parameter-less particles that are always safe to spawn
        PARTICLE_CANDIDATES.add(ParticleTypes.PORTAL);
        PARTICLE_CANDIDATES.add(ParticleTypes.ENCHANT);
        PARTICLE_CANDIDATES.add(ParticleTypes.END_ROD);
        PARTICLE_CANDIDATES.add(ParticleTypes.SMOKE);
        PARTICLE_CANDIDATES.add(ParticleTypes.SOUL);
        PARTICLE_CANDIDATES.add(ParticleTypes.SOUL_FIRE_FLAME);
        PARTICLE_CANDIDATES.add(ParticleTypes.FLAME);
        PARTICLE_CANDIDATES.add(ParticleTypes.WITCH);
        PARTICLE_CANDIDATES.add(ParticleTypes.CRIT);
        PARTICLE_CANDIDATES.add(ParticleTypes.ENCHANTED_HIT);
        PARTICLE_CANDIDATES.add(ParticleTypes.ASH);
        PARTICLE_CANDIDATES.add(ParticleTypes.MYCELIUM);
        PARTICLE_CANDIDATES.add(ParticleTypes.ELECTRIC_SPARK);
        PARTICLE_CANDIDATES.add(ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
        PARTICLE_CANDIDATES.add(ParticleTypes.REVERSE_PORTAL);
    }
}
