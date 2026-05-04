package eu.cizmetari.bgm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.cizmetari.bgm.sound.SoundEntry;
import eu.cizmetari.bgm.sound.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("bgm")
                        .then(
                                Commands.literal("play")
                                        .then(
                                                Commands.argument("file", StringArgumentType.string())
                                                        .executes(ctx -> play(ctx, false))
                                                        .then(Commands.argument("loop", BoolArgumentType.bool())
                                                                .executes(ctx -> play(ctx, BoolArgumentType.getBool(ctx, "loop")))
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("stop_id")
                                        .then(
                                                Commands.argument("id", IntegerArgumentType.integer())
                                                        .executes(ctx -> {
                                                            int id = IntegerArgumentType.getInteger(ctx, "id");
                                                            if (!(BackgroundMusicMod.PLAYING_SOUNDS.containsKey(id))) {
                                                                throw new SimpleCommandExceptionType(Component.literal("ID is not valid!")).create();
                                                            }
                                                            Sounds.stopSound(id);
                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                Commands.literal("list")
                                        .executes(ctx -> {
                                            ArrayList<String> lines = new ArrayList<>();
                                            for (Map.Entry<Integer, SoundEntry> entry : BackgroundMusicMod.PLAYING_SOUNDS.entrySet()) {
                                                lines.add(String.format("%s (%s)", entry.getKey(), entry.getValue().playingFile()));
                                            }
                                            if (!lines.isEmpty()) {
                                                Minecraft.getInstance().player.sendSystemMessage(Component.literal(String.join("\n", lines)));
                                            }
                                            return 1;
                                        })
                        )
                        .then(
                                Commands.literal("stop_all")
                                        .executes(ctx -> {BackgroundMusicMod.clearSounds();return 1;})
                        )
                        .then(
                                Commands.literal("stop_file")
                                        .then(
                                                Commands.argument("file", StringArgumentType.string())
                                                        .executes(ctx -> {
                                                            Path path;
                                                            try {
                                                                path = Path.of(StringArgumentType.getString(ctx, "file")).toAbsolutePath().normalize();
                                                            } catch (InvalidPathException e) {
                                                                throw new SimpleCommandExceptionType(
                                                                        Component.literal("Invalid file path!")
                                                                ).create();
                                                            }
                                                            if (Files.notExists(path)) {
                                                                throw new SimpleCommandExceptionType(Component.literal("File does not exist!")).create();
                                                            }
                                                            for (Map.Entry<Integer, SoundEntry> kv : BackgroundMusicMod.PLAYING_SOUNDS.entrySet()) {
                                                                if (kv.getValue().playingFile().equals(path)) {
                                                                    Sounds.stopSound(kv.getKey());
                                                                }
                                                            }
                                                            return 1;
                                                        })
                                        )
                        )
                        .then(Commands.literal("debug")
                                .then(Commands.literal("pwd")
                                        .executes(ctx -> {
                                            Minecraft.getInstance().player.sendSystemMessage(Component.literal(System.getProperty("user.dir")));
                                            return 1;
                                        }))
                                .then(Commands.literal("whoami")
                                        .executes(ctx -> {
                                            Minecraft.getInstance().player.sendSystemMessage(Component.literal(System.getProperty("user.name")));
                                            return 1;
                                        }))
                        )
        );
    }

    // don't hate on brigadier, these are the limits of oop
    private static int play(CommandContext<CommandSourceStack> ctx, boolean loop) throws CommandSyntaxException {
        Path path;
        try {
            path = Path.of(StringArgumentType.getString(ctx, "file")).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new SimpleCommandExceptionType(
                    Component.literal("Invalid file path!")
            ).create();
        }
        if (Files.notExists(path)) {
            throw new SimpleCommandExceptionType(Component.literal("File does not exist!")).create();
        }
        try {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(String.valueOf(Sounds.playSound(path, loop))));
        } catch (RuntimeException e) {
            throw new SimpleCommandExceptionType(e::getMessage).create();
        }
        return 1;
    }
}
