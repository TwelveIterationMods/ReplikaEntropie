package net.blay09.mods.replikaentropie.mixin;

import net.blay09.mods.replikaentropie.util.UnpackedLootTableHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(RandomizableContainerBlockEntity.class)
public class RandomizableContainerBlockEntityMixin implements UnpackedLootTableHolder {

    private ResourceLocation unpackedLootTable;

    @Inject(method = "unpackLootTable", at = @At("HEAD"))
    private void unpackLootTable(Player player, CallbackInfo ci) {
        final var lootTable = ((RandomizableContainerBlockEntityAccessor) this).getLootTable();
        if (lootTable != null) {
            this.unpackedLootTable = lootTable;
        }
    }

    @Override
    public Optional<ResourceLocation> getUnpackedLootTable() {
        return Optional.ofNullable(unpackedLootTable);
    }
}
