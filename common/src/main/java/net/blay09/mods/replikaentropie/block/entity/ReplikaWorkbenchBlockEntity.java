package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.menu.ReplikaWorkbenchMenu;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
            return slot != 4 || !itemStack.is(ModItemTags.REPLIKA_FRAME);
        }
    };

    public ReplikaWorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.replikaWorkbench.get(), pos, blockState);
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.replika_workbench");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new ReplikaWorkbenchMenu(i, inventory, backingContainer);
            }
        };
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }
}
