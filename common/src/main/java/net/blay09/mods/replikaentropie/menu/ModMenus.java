package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.balm.world.BalmMenuFactory;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
    public static Holder<MenuType<ReplikaWorkbenchMenu>> replikaWorkbench;
    public static Holder<MenuType<ResearchMenu>> research;
    public static Holder<MenuType<FabricatorMenu>> fabricator;
    public static Holder<MenuType<AssemblerMenu>> assembler;
    public static Holder<MenuType<CobblescrapMenu>> cobblescrap;
    public static Holder<MenuType<LavascrapMenu>> lavascrap;
    public static Holder<MenuType<LavaSinkMenu>> lavaSink;
    public static Holder<MenuType<WorldEaterMenu>> worldEater;
    public static Holder<MenuType<FragmentAcceleratorMenu>> fragmentAccelerator;
    public static Holder<MenuType<FragmentalGeneratorMenu>> fragmentalGenerator;
    public static Holder<MenuType<BiomassHarvesterMenu>> biomassHarvester;
    public static Holder<MenuType<BiomassIncubatorMenu>> biomassIncubator;
    public static Holder<MenuType<EntropicDataMinerMenu>> entropicDataMiner;
    public static Holder<MenuType<RecyclerMenu>> recycler;
    public static Holder<MenuType<AbstractNonogramMenu>> nonogram;
    public static Holder<MenuType<AbstractNonogramMenu>> nonogramEditor;

    public static void initialize(BalmMenuTypeRegistrar menus) {
        replikaWorkbench = menus.register("replika_workbench", new BalmMenuFactory<ReplikaWorkbenchMenu, Unit>() {
            @Override
            public ReplikaWorkbenchMenu create(int syncId, Inventory inventory, Unit unit) {
                return new ReplikaWorkbenchMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        research = menus.register("research", new BalmMenuFactory<ResearchMenu, ResearchMenu.Data>() {
            @Override
            public ResearchMenu create(int syncId, Inventory inventory, ResearchMenu.Data data) {
                return new ResearchMenu(syncId, inventory, data);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ResearchMenu.Data> getStreamCodec() {
                return ResearchMenu.Data.STREAM_CODEC;
            }
        }).asHolder();

        fabricator = menus.register("fabricator", new BalmMenuFactory<FabricatorMenu, Unit>() {
            @Override
            public FabricatorMenu create(int syncId, Inventory inventory, Unit unit) {
                return new FabricatorMenu(syncId, inventory, FabricatorRecipe.getRecipes(inventory.player.level()));
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        assembler = menus.register("assembler", new BalmMenuFactory<AssemblerMenu, Unit>() {
            @Override
            public AssemblerMenu create(int syncId, Inventory inventory, Unit unit) {
                return new AssemblerMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        cobblescrap = menus.register("cobblescrap", new BalmMenuFactory<CobblescrapMenu, Unit>() {
            @Override
            public CobblescrapMenu create(int syncId, Inventory inventory, Unit unit) {
                return new CobblescrapMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        lavascrap = menus.register("lavascrap", new BalmMenuFactory<LavascrapMenu, Unit>() {
            @Override
            public LavascrapMenu create(int syncId, Inventory inventory, Unit unit) {
                return new LavascrapMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        lavaSink = menus.register("lava_sink", new BalmMenuFactory<LavaSinkMenu, Unit>() {
            @Override
            public LavaSinkMenu create(int syncId, Inventory inventory, Unit unit) {
                return new LavaSinkMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        worldEater = menus.register("world_eater", new BalmMenuFactory<WorldEaterMenu, Unit>() {
            @Override
            public WorldEaterMenu create(int syncId, Inventory inventory, Unit unit) {
                return new WorldEaterMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        fragmentAccelerator = menus.register("fragment_accelerator", new BalmMenuFactory<FragmentAcceleratorMenu, Unit>() {
            @Override
            public FragmentAcceleratorMenu create(int syncId, Inventory inventory, Unit unit) {
                return new FragmentAcceleratorMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        fragmentalGenerator = menus.register("fragmental_generator", new BalmMenuFactory<FragmentalGeneratorMenu, Unit>() {
            @Override
            public FragmentalGeneratorMenu create(int syncId, Inventory inventory, Unit unit) {
                return new FragmentalGeneratorMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        biomassHarvester = menus.register("biomass_harvester", new BalmMenuFactory<BiomassHarvesterMenu, Unit>() {
            @Override
            public BiomassHarvesterMenu create(int syncId, Inventory inventory, Unit unit) {
                return new BiomassHarvesterMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        biomassIncubator = menus.register("biomass_incubator", new BalmMenuFactory<BiomassIncubatorMenu, Unit>() {
            @Override
            public BiomassIncubatorMenu create(int syncId, Inventory inventory, Unit unit) {
                return new BiomassIncubatorMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        entropicDataMiner = menus.register("entropic_data_miner", new BalmMenuFactory<EntropicDataMinerMenu, EntropicDataMinerMenu.Data>() {
            @Override
            public EntropicDataMinerMenu create(int syncId, Inventory inventory, EntropicDataMinerMenu.Data data) {
                return new EntropicDataMinerMenu(syncId, data);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, EntropicDataMinerMenu.Data> getStreamCodec() {
                return EntropicDataMinerMenu.Data.STREAM_CODEC;
            }
        }).asHolder();

        recycler = menus.register("recycler", new BalmMenuFactory<RecyclerMenu, Unit>() {
            @Override
            public RecyclerMenu create(int syncId, Inventory inventory, Unit unit) {
                return new RecyclerMenu(syncId, inventory);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        }).asHolder();

        nonogram = menus.register("nonogram", new BalmMenuFactory<AbstractNonogramMenu, NonogramMenu.Data>() {
            @Override
            public AbstractNonogramMenu create(int syncId, Inventory inventory, NonogramMenu.Data data) {
                return new NonogramMenu(syncId, inventory, data);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        }).asHolder();

        nonogramEditor = menus.register("nonogram_editor", new BalmMenuFactory<AbstractNonogramMenu, NonogramMenu.Data>() {
            @Override
            public AbstractNonogramMenu create(int syncId, Inventory inventory, NonogramMenu.Data data) {
                return new NonogramEditorMenu(syncId, data);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        }).asHolder();
    }
}
