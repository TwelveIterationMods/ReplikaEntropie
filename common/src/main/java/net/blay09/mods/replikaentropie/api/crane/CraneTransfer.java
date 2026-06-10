package net.blay09.mods.replikaentropie.api.crane;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record CraneTransfer(BlockState state, @Nullable CompoundTag blockEntityData) {
}
