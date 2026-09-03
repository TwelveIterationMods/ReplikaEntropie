package net.blay09.mods.replikaentropie.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.nonogram.Nonogram;
import net.blay09.mods.replikaentropie.core.research.ResearchManagers;
import net.blay09.mods.replikaentropie.core.research.ResearchState;
import net.blay09.mods.replikaentropie.menu.NonogramEditorMenu;
import net.blay09.mods.replikaentropie.menu.NonogramMenu;
import net.blay09.mods.replikaentropie.registry.ModDynamicRegistries;
import net.blay09.mods.replikaentropie.registry.Research;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Collection;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaEntropieCommand {

    private static final SimpleCommandExceptionType INVALID_RESEARCH = new SimpleCommandExceptionType(Component.translatable("commands.replikaentropie.research.invalidResearch"));
    private static final SimpleCommandExceptionType INVALID_NONOGRAM = new SimpleCommandExceptionType(Component.translatable("commands.replikaentropie.nonogram.invalidNonogram"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("replikaentropie")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("scans")
                        .then(Commands.literal("reset")
                                .executes(ReplikaEntropieCommand::resetScans)))
                .then(Commands.literal("research")
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("id", ResourceKeyArgument.key(ModDynamicRegistries.RESEARCH))
                                                .executes(ctx -> researchUnlock(ctx, EntityArgument.getPlayers(ctx, "targets"), ctx.getArgument("id", ResourceKey.class))))
                                        .then(Commands.literal("all").executes(ctx -> researchUnlockAll(ctx, EntityArgument.getPlayers(ctx, "targets")))))
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("id", ResourceKeyArgument.key(ModDynamicRegistries.RESEARCH))
                                                .executes(ctx -> researchReset(ctx, EntityArgument.getPlayers(ctx, "targets"), ctx.getArgument("id", ResourceKey.class))))
                                        .then(Commands.literal("all").executes(ctx -> researchResetAll(ctx, EntityArgument.getPlayers(ctx, "targets")))))
                        )
                )
                .then(Commands.literal("nonogram")
                        .then(Commands.literal("create")
                                .then(Commands.argument("width", IntegerArgumentType.integer(1, 15))
                                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 10))
                                                .then(Commands.argument("id", IdentifierArgument.id())
                                                        .executes(ctx -> createNonogram(ctx, IdentifierArgument.getId(ctx, "id"), IntegerArgumentType.getInteger(ctx, "width"), IntegerArgumentType.getInteger(ctx, "height")))
                                                ))))
                        .then(Commands.literal("view")
                                .then(Commands.argument("id", ResourceKeyArgument.key(ModDynamicRegistries.NONOGRAM))
                                        .executes(ctx -> viewNonogram(ctx, ResourceKeyArgument.getRegistryKey(ctx, "id", ModDynamicRegistries.NONOGRAM)))
                                ))
                        .then(Commands.literal("play")
                                .then(Commands.argument("id", ResourceKeyArgument.key(ModDynamicRegistries.NONOGRAM))
                                        .executes(ctx -> playNonogram(ctx, ResourceKeyArgument.getRegistryKey(ctx, "id", ModDynamicRegistries.NONOGRAM)))
                                ))
                        .then(Commands.literal("edit")
                                .then(Commands.argument("id", ResourceKeyArgument.key(ModDynamicRegistries.NONOGRAM))
                                        .executes(ctx -> editNonogram(ctx, ResourceKeyArgument.getRegistryKey(ctx, "id", ModDynamicRegistries.NONOGRAM)))))
                )
        );
    }

    private static int resetScans(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Analyzer.resetAllAnalyzed(player);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.scans.reset"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchUnlock(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, ResourceKey<Research> researchKey) throws CommandSyntaxException {
        ModDynamicRegistries.research(context.getSource().registryAccess()).get(researchKey).orElseThrow(INVALID_RESEARCH::create);
        final var researchId = researchKey.identifier();
        targets.forEach(player -> ResearchManagers.updateResearch(player, researchId, ResearchState.UNLOCKED));
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.unlock", researchId.toString()), false);
        return targets.size();
    }

    private static int researchUnlockAll(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) {
        final var registry = ModDynamicRegistries.research(context.getSource().registryAccess());
        targets.forEach(player -> registry.keySet()
                .forEach(id -> ResearchManagers.updateResearch(player, id, ResearchState.UNLOCKED)));
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.unlockAll"), false);
        return targets.size();
    }

    private static int researchReset(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, ResourceKey<Research> researchKey) throws CommandSyntaxException {
        ModDynamicRegistries.research(context.getSource().registryAccess()).get(researchKey).orElseThrow(INVALID_RESEARCH::create);
        final var researchId = researchKey.identifier();
        targets.forEach(player -> ResearchManagers.updateResearch(player, researchId, ResearchState.NONE));
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.reset", researchId.toString()), false);
        return targets.size();
    }

    private static int researchResetAll(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) {
        targets.forEach(ResearchManagers::resetAllResearch);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.resetAll"), false);
        return targets.size();
    }

    private static int createNonogram(CommandContext<CommandSourceStack> context, Identifier id, int width, int height) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = new Nonogram(width, height, new int[width * height]);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramEditorMenu(containerId, nonogram, nonogram.createState(), id);
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.create", id.toString(), width, height), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int viewNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = getNonogram(context, id);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramMenu(containerId, inventory, nonogram, nonogram.createState());
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createCompletedState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.view", id.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int playNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = getNonogram(context, id);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramMenu(containerId, inventory, nonogram, nonogram.createState());
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.play", id.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int editNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = getNonogram(context, id);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramEditorMenu(containerId, nonogram, nonogram.createCompletedState(), id);
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createCompletedState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.edit", id.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Nonogram getNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var nonogram = ModDynamicRegistries.nonograms(context.getSource().registryAccess()).getValue(id);
        if (nonogram == null) {
            throw INVALID_NONOGRAM.create();
        }
        return nonogram;
    }

}
