package com.davenonymous.libnonymous.commands;

import com.davenonymous.libnonymous.network.Networking;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.command.ModIdArgument;

public class CommandMods implements Command<CommandSourceStack> {
	private static final CommandMods CMD = new CommandMods();

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("mods").executes(CMD)
				.then(Commands.argument("mod", ModIdArgument.modIdArgument()).executes(CMD)
						.then(Commands.argument("openConfig", BoolArgumentType.bool()).executes(CMD)));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		try {
			var modArg = context.getArgument("mod", String.class);
			var openConfigScreen = false;
			try {
				openConfigScreen = context.getArgument("openConfig", Boolean.class);
			} catch (IllegalArgumentException ignored) {
			}

			Networking.sendOpenModsGUI(context.getSource().getPlayerOrException().connection.connection, modArg, openConfigScreen);
			return 0;
		} catch (IllegalArgumentException ignored) {
		}

		Networking.sendOpenModsGUI(context.getSource().getPlayerOrException().connection.connection, "", false);
		return 0;
	}
}