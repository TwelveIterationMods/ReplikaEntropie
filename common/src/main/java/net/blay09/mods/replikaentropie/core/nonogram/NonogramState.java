package net.blay09.mods.replikaentropie.core.nonogram;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NonogramState(int width, int height, int[] marks) {
    public static final StreamCodec<RegistryFriendlyByteBuf, NonogramState> STREAM_CODEC = StreamCodec.of(NonogramState::write, NonogramState::read);

    public NonogramState(int width, int height) {
        this(width, height, new int[width * height]);
    }

    public static NonogramState read(CompoundTag compound) {
        final var width = compound.getIntOr("Width", 10);
        final var height = compound.getIntOr("Height", 10);
        final var marks = compound.getIntArray("Marks").orElseGet(() -> new int[width * height]);
        return new NonogramState(width, height, marks);
    }

    public static void write(FriendlyByteBuf buf, NonogramState state) {
        buf.writeVarInt(state.width);
        buf.writeVarInt(state.height);
        buf.writeVarIntArray(state.marks);
    }

    public static NonogramState read(FriendlyByteBuf buf) {
        return new NonogramState(buf.readVarInt(), buf.readVarInt(), buf.readVarIntArray());
    }

    public Tag write() {
        final var compound = new CompoundTag();
        compound.putInt("Width", width);
        compound.putInt("Height", height);
        compound.putIntArray("Marks", marks);
        return compound;
    }

    private int index(int column, int row) {
        return row * width + column;
    }

    public void mark(int column, int row, int mark) {
        marks[index(column, row)] = mark;
    }

    public int mark(int column, int row) {
        return marks[index(column, row)];
    }

}
