package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.replikaentropie.menu.ReplikaWorkbenchMenu;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ReplikaWorkbenchBlockEntity extends BlockEntity implements BalmContainerProvider {

    private final DefaultContainer backingContainer = new DefaultContainer(9) {
        @Override
        public void setChanged() {
            ReplikaWorkbenchBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            if (slot == 4 && !getItem(slot).isEmpty()) {
                return false;
            }
            return super.canPlaceItem(slot, itemStack);
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack itemStack) {
            return true;
        }
    };

    public ReplikaWorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.replikaWorkbench.value(), pos, blockState);
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider<Unit>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.replika_workbench");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new ReplikaWorkbenchMenu(i, inventory, backingContainer);
            }

            @Override
            public Unit getScreenOpeningData(ServerPlayer player) {
                return Unit.INSTANCE;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        };
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }
}
