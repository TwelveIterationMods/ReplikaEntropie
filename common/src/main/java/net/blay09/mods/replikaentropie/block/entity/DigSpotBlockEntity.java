package net.blay09.mods.replikaentropie.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

public class DigSpotBlockEntity extends BlockEntity {

    private @Nullable ResourceKey<LootTable> lootTable;

    public DigSpotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.digSpot.value(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.storeNullable("loot_table", ResourceKey.codec(Registries.LOOT_TABLE), lootTable);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        lootTable = input.read("loot_table", ResourceKey.codec(Registries.LOOT_TABLE)).orElse(null);
    }

    public @Nullable ResourceKey<LootTable> getLootTable() {
        return lootTable;
    }

    public void setLootTable(ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }
}
