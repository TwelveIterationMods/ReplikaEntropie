package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.replikaentropie.component.AssemblyTicket;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.blay09.mods.replikaentropie.core.research.ResearchManagers;
import net.blay09.mods.replikaentropie.core.research.ResearchState;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.slot.ResearchCostSlot;
import net.blay09.mods.replikaentropie.menu.slot.ResearchEntrySlot;
import net.blay09.mods.replikaentropie.registry.ModResearch;
import net.blay09.mods.replikaentropie.registry.Research;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Locale;
import java.util.function.IntFunction;

public class ResearchMenu extends AbstractContainerMenu {

    public enum MenuResearchState {
        MISSING_DEPENDENCIES(4),
        MISSING_INGREDIENTS(3),
        AVAILABLE(2),
        IN_PROGRESS(1),
        UNLOCKED(0);

        public static final IntFunction<MenuResearchState> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int priority;

        MenuResearchState(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return priority;
        }

        public boolean requiresPayment() {
            return this == AVAILABLE || this == MISSING_INGREDIENTS || this == MISSING_DEPENDENCIES;
        }
    }

    public record StatefulResearchEntry(Identifier id, Research research, MenuResearchState state) {

        public static final StreamCodec<RegistryFriendlyByteBuf, StatefulResearchEntry> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC,
                StatefulResearchEntry::id,
                Research.STREAM_CODEC,
                StatefulResearchEntry::research,
                ByteBufCodecs.idMapper(MenuResearchState.BY_ID, MenuResearchState::ordinal).cast(),
                StatefulResearchEntry::state,
                StatefulResearchEntry::new
        );

        public Component title() {
            return Component.translatable(id.toLanguageKey("research", "title"));
        }

        public Component description() {
            return Component.translatable(id.toLanguageKey("research", "description"));
        }
    }

    public record Data(List<StatefulResearchEntry> entries, int dataCollected) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                StatefulResearchEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
                Data::entries,
                ByteBufCodecs.VAR_INT,
                Data::dataCollected,
                Data::new
        );
    }

    private final List<ResearchEntrySlot> researchSlots = new ArrayList<>();
    private final ResearchCostSlot dataCostSlot;
    private final ResearchCostSlot scrapCostSlot;
    private final ResearchCostSlot biomassCostSlot;
    private final ResearchCostSlot fragmentsCostSlot;

    private final Inventory playerInventory;
    private final List<StatefulResearchEntry> researchEntries;
    private final int dataCollected;

    private final List<StatefulResearchEntry> filteredEntries;
    private final Comparator<StatefulResearchEntry> currentSorting =
            Comparator.comparingInt((StatefulResearchEntry it) -> it.state().priority())
                    .thenComparingInt(it -> it.research().sortOrder())
                    .thenComparing(StatefulResearchEntry::id);

    @Nullable
    private StatefulResearchEntry clientSelectedResearch;

    private boolean scrollOffsetDirty;
    private int scrollOffset;

    public ResearchMenu(int containerId, Inventory playerInventory, Data data) {
        super(ModMenus.research.value(), containerId);
        this.playerInventory = playerInventory;
        researchEntries = data.entries();
        dataCollected = data.dataCollected();

        filteredEntries = new ArrayList<>(researchEntries);
        filteredEntries.sort(currentSorting);

        dataCostSlot = new ResearchCostSlot(143, 133, ResearchCostSlot.Type.DATA);
        addSlot(dataCostSlot);
        scrapCostSlot = new ResearchCostSlot(169, 133, ResearchCostSlot.Type.SCRAP);
        addSlot(scrapCostSlot);
        biomassCostSlot = new ResearchCostSlot(194, 133, ResearchCostSlot.Type.BIOMASS);
        addSlot(biomassCostSlot);
        fragmentsCostSlot = new ResearchCostSlot(219, 133, ResearchCostSlot.Type.FRAGMENTS);
        addSlot(fragmentsCostSlot);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                final var slot = new ResearchEntrySlot(12 + j * (18 + 7), 25 + i * (18 + 7));
                researchSlots.add(slot);
                addSlot(slot);
            }
        }

        updateResearchSlots();
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput clickType, Player player) {
        final var slot = slotId >= 0 && slotId < slots.size() ? slots.get(slotId) : null;
        if (player.level().isClientSide() && slot instanceof ResearchEntrySlot researchEntrySlot) {
            final var researchEntry = researchEntrySlot.getResearchEntry();
            if (researchEntry != null) {
                setClientSelectedResearch(researchEntry);
            }
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    private void setClientSelectedResearch(@Nullable StatefulResearchEntry researchEntry) {
        clientSelectedResearch = researchEntry;
        updateCostSlots(researchEntry);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        final var entry = buttonId >= 0 && buttonId < researchEntries.size() ? researchEntries.get(buttonId) : null;
        if (entry != null) {
            if (entry.state() == MenuResearchState.AVAILABLE) {
                final var research = entry.research();
                if (research.dataCost() > 0) {
                    Analyzer.getManager(player).grantData(player, -research.dataCost());
                }
                final var inventory = player.getInventory();
                if (research.scrapCost() > 0) {
                    inventory.clearOrCountMatchingItems(it -> it.is(ModItems.scrap.asItem()), false, research.scrapCost(), inventory);
                }
                if (research.biomassCost() > 0) {
                    inventory.clearOrCountMatchingItems(it -> it.is(ModItems.biomass.asItem()), false, research.biomassCost(), inventory);
                }
                if (research.fragmentsCost() > 0) {
                    inventory.clearOrCountMatchingItems(it -> it.is(ModItems.fragments.asItem()), false, research.fragmentsCost(), inventory);
                }
                player.inventoryMenu.broadcastChanges();
                ResearchManagers.updateResearch(player, entry.id(), ResearchState.IN_PROGRESS);
                openNonogram(player, entry);
            } else if (entry.state() == MenuResearchState.IN_PROGRESS) {
                openNonogram(player, entry);
            } else if (entry.state() == MenuResearchState.UNLOCKED) {
                final var itemStack = printAssemblyTicket(entry);
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, false, Prediction.SERVER_ONLY);
                }
                player.inventoryMenu.broadcastChanges();
            }
        }
        return false;
    }

    private ItemStack printAssemblyTicket(StatefulResearchEntry entry) {
        final var itemStack = ModItems.assemblyTicket.createStack();
        itemStack.set(DataComponents.CUSTOM_NAME, entry.title());
        final var research = entry.research();
        if (!research.unlockedRecipes().isEmpty()) {
            itemStack.set(ModDataComponents.assemblyTicket(), new AssemblyTicket(research.unlockedRecipes().getFirst(), 1));
        }
        return itemStack;
    }

    public void openNonogram(Player player, StatefulResearchEntry entry) {
        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return entry.title();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                final var researchId = entry.id();
                final var nonogram = NonogramLoader.getNonogram(entry.research().nonogram())
                        .orElseGet(NonogramLoader::createFallback);
                final var nonogramState = ResearchManagers.getNonogramState(player, researchId)
                        .map(nonogram::ensureState)
                        .orElseGet(nonogram::createState);
                return new NonogramResearchMenu(containerId, inventory, nonogram, nonogramState, researchId);
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                final var nonogram = NonogramLoader.getNonogram(entry.research().nonogram())
                        .orElseGet(NonogramLoader::createFallback);
                final var nonogramState = ResearchManagers.getNonogramState(player, entry.id())
                        .map(nonogram::ensureState)
                        .orElseGet(nonogram::createState);
                return new NonogramMenu.Data(nonogram.clues(), nonogramState);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });
    }

    public void updateResearchSlots() {
        int i = scrollOffset * 4;
        for (final var slot : researchSlots) {
            if (i < filteredEntries.size()) {
                final var entry = filteredEntries.get(i);
                slot.setResearchEntry(entry);
                i++;
            } else {
                slot.setResearchEntry(null);
            }
        }
    }

    public void updateCostSlots(@Nullable StatefulResearchEntry statefulEntry) {
        if (statefulEntry == null) {
            dataCostSlot.setCost(0);
            scrapCostSlot.setCost(0);
            biomassCostSlot.setCost(0);
            fragmentsCostSlot.setCost(0);
            return;
        }

        final var research = statefulEntry.research();
        final var data = research.dataCost();
        final var scrap = research.scrapCost();
        final var biomass = research.biomassCost();
        final var fragments = research.fragmentsCost();
        dataCostSlot.setCost(statefulEntry.state().requiresPayment() ? data : 0);
        dataCostSlot.setAvailable(Mth.clamp(dataCollected, 0, data));
        scrapCostSlot.setCost(statefulEntry.state().requiresPayment() ? scrap : 0);
        scrapCostSlot.setAvailable(playerInventory.countItem(ModItems.scrap.asItem()));
        biomassCostSlot.setCost(statefulEntry.state().requiresPayment() ? biomass : 0);
        biomassCostSlot.setAvailable(playerInventory.countItem(ModItems.biomass.asItem()));
        fragmentsCostSlot.setCost(statefulEntry.state().requiresPayment() ? fragments : 0);
        fragmentsCostSlot.setAvailable(playerInventory.countItem(ModItems.fragments.asItem()));
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        updateResearchSlots();
    }

    public int getEntryCount() {
        return filteredEntries.size();
    }

    public boolean isScrollOffsetDirty() {
        return scrollOffsetDirty;
    }

    public void setScrollOffsetDirty(boolean dirty) {
        scrollOffsetDirty = dirty;
    }

    @Nullable
    public StatefulResearchEntry getClientSelectedResearch() {
        return clientSelectedResearch;
    }

    public int getClientSelectedResearchIndex() {
        return researchEntries.indexOf(clientSelectedResearch);
    }

    public boolean clientCanUnlockSelected() {
        return clientSelectedResearch != null
                && clientSelectedResearch.state() == MenuResearchState.AVAILABLE;
    }

    public boolean clientCanDecryptSelected() {
        return clientSelectedResearch != null
                && clientSelectedResearch.state() == MenuResearchState.IN_PROGRESS;
    }

    public boolean clientCanPrintSelected() {
        if (clientSelectedResearch == null || clientSelectedResearch.state() != MenuResearchState.UNLOCKED) {
            return false;
        }

        final var research = clientSelectedResearch.research();
        return research.type() == Research.Type.ASSEMBLER && !research.unlockedRecipes().isEmpty();
    }

    public boolean clientMissingIngredients() {
        return clientSelectedResearch != null
                && clientSelectedResearch.state() == ResearchMenu.MenuResearchState.MISSING_INGREDIENTS;
    }

    public int getDataCollected() {
        return dataCollected;
    }

    public void setSearchQuery(String query) {
        final var lowercaseQuery = query.trim().toLowerCase(Locale.ROOT);
        filteredEntries.clear();
        if (lowercaseQuery.isEmpty()) {
            filteredEntries.addAll(researchEntries);
        } else {
            for (final var entry : researchEntries) {
                final var title = entry.title().getString().toLowerCase(Locale.ROOT);
                if (title.contains(lowercaseQuery)) {
                    filteredEntries.add(entry);
                }
            }
        }

        filteredEntries.sort(currentSorting);
        setScrollOffset(0);
        setScrollOffsetDirty(true);
    }

    public int getFilteredIndexOf(Identifier id) {
        for (int i = 0; i < filteredEntries.size(); i++) {
            final var entry = filteredEntries.get(i);
            if (entry.id().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static class Provider implements BalmMenuProvider<ResearchMenu.Data> {

        @Override
        public Component getDisplayName() {
            return Component.translatable("container.replikaentropie.sky_scraper");
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
            return new ResearchMenu(containerId, inventory, createMenuData(player));
        }

        @Override
        public Data getScreenOpeningData(ServerPlayer player) {
            return createMenuData(player);
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Data> getScreenStreamCodec() {
            return ResearchMenu.Data.STREAM_CODEC;
        }

        private static ResearchMenu.Data createMenuData(Player player) {
            final var dataCollected = Analyzer.getManager(player).getDataCollected(player);
            if (!(player.level() instanceof ServerLevel serverLevel)) {
                return new ResearchMenu.Data(List.of(), dataCollected);
            }

            final var researchManager = ResearchManagers.getManager(player);

            final var entries = ModResearch.registry(serverLevel.registryAccess()).listElements()
                    .map(researchHolder -> {
                        final var id = researchHolder.key().identifier();
                        final var research = researchHolder.value();
                        final var state = researchManager.getResearchState(player, id);
                        if (state == ResearchState.NONE) {
                            if (!researchManager.meetsDependencies(player, research.hardDependencies())) {
                                return null;
                            }
                        }

                        final var menuState = switch (state) {
                            case NONE -> {
                                if (!researchManager.meetsDependencies(player, research.softDependencies())) {
                                    yield MenuResearchState.MISSING_DEPENDENCIES;
                                }
                                if (!canAfford(player, research)) {
                                    yield MenuResearchState.MISSING_INGREDIENTS;
                                }
                                yield MenuResearchState.AVAILABLE;
                            }
                            case IN_PROGRESS -> MenuResearchState.IN_PROGRESS;
                            default -> MenuResearchState.UNLOCKED;
                        };

                        return new ResearchMenu.StatefulResearchEntry(id, research, menuState);
                    })
                    .filter(Objects::nonNull)
                    .toList();
            return new ResearchMenu.Data(entries, dataCollected);
        }

        private static boolean canAfford(Player player, Research research) {
            final var dataCollected = Analyzer.getManager(player).getDataCollected(player);
            final var scrap = player.getInventory().countItem(ModItems.scrap.asItem());
            final var biomass = player.getInventory().countItem(ModItems.biomass.asItem());
            final var fragments = player.getInventory().countItem(ModItems.fragments.asItem());
            return dataCollected >= research.dataCost()
                    && scrap >= research.scrapCost()
                    && biomass >= research.biomassCost()
                    && fragments >= research.fragmentsCost();
        }
    }

}
