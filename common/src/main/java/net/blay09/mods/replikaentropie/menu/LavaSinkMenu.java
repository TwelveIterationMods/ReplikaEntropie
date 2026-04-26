package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.LavaSinkBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.IngredientSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class LavaSinkMenu extends AbstractScrapGeneratorMenu {

    public static final int DATA_LAVA_TANK = AbstractScrapGeneratorMenu.DATA_COUNT;
    public static final int DATA_MAX_LAVA_TANK = AbstractScrapGeneratorMenu.DATA_COUNT + 1;
    public static final int DATA_COUNT = AbstractScrapGeneratorMenu.DATA_COUNT + 2;

    private final QuickMove.Routing quickMove;

    public LavaSinkMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(LavaSinkBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public LavaSinkMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.lavaSink.value(), containerId, playerInventory, container, data);

        checkContainerSize(container, LavaSinkBlockEntity.CONTAINER_SIZE);

        addSlot(new IngredientSlot(container, LavaSinkBlockEntity.INPUT_SLOT, 51, 38, Ingredient.of(Items.COBBLESTONE)));
        addSlot(new Slot(container, LavaSinkBlockEntity.BUCKET_SLOT, 110, 79) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.BUCKET);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addPlayerInventorySlots(31);

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("input", LavaSinkBlockEntity.INPUT_SLOT)
                .slot("bucket", LavaSinkBlockEntity.BUCKET_SLOT)
                .route(it -> it.is(Items.COBBLESTONE), QuickMove.PLAYER, "input")
                .route(it -> it.is(Items.BUCKET), QuickMove.PLAYER, "bucket")
                .build();

        container.startOpen(playerInventory.player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public int getLavaTank() {
        return data.get(DATA_LAVA_TANK);
    }

    public int getMaxLavaCapacity() {
        return data.get(DATA_MAX_LAVA_TANK);
    }

    public float getLavaTankProgress() {
        return getMaxLavaCapacity() > 0 ? (float) getLavaTank() / getMaxLavaCapacity() : 0f;
    }
}
