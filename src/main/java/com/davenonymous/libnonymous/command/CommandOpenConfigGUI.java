package com.davenonymous.libnonymous.command;

import com.davenonymous.libnonymous.network.Networking;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

public class CommandOpenConfigGUI implements Command<CommandSource> {
    private static final CommandOpenConfigGUI CMD = new CommandOpenConfigGUI();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("config")
            .requires(cs -> cs.hasPermissionLevel(0))
            .then(
                Commands.argument("modid", ModIdArgument.modIdArgument())
                    .then(
                        Commands.argument("mode", EnumArgument.enumArgument(Mode.class))
                            .executes(CMD)
                    )
                    .executes(context -> {
                        final String modId = context.getArgument("modid", String.class);
                        ServerPlayerEntity player = context.getSource().asPlayer();
                        Networking.openConfigGui(player, modId, Mode.BY_SPEC);
                        return 0;
                    })
            )
            .executes(context -> {
                ServerPlayerEntity player = context.getSource().asPlayer();
                Networking.openConfigGui(player);
                return 0;
            });
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        final String modId = context.getArgument("modid", String.class);
        Mode mode = context.getArgument("mode", Mode.class);

        ServerPlayerEntity player = context.getSource().asPlayer();
        Networking.openConfigGui(player, modId, mode);
        return 0;
    }

    public enum Mode {
        NATIVE,
        BY_SPEC
    }
}
