package com.davenonymous.libnonymous.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class SimpleCommandReply implements Command<CommandSourceStack> {
	private final Component reply;
	private final boolean isError;

	public SimpleCommandReply(Component reply, boolean isError) {
		this.reply = reply;
		this.isError = isError;
	}

	public static SimpleCommandReply info(String input) {
		return new SimpleCommandReply(Component.literal(input), false);
	}

	public static SimpleCommandReply info(String fmt, Object... data) {
		return new SimpleCommandReply(Component.translatable(fmt, data), false);
	}

	public static SimpleCommandReply error(String input) {
		return new SimpleCommandReply(Component.literal(input), true);
	}

	public static SimpleCommandReply error(String fmt, Object... data) {
		return new SimpleCommandReply(Component.translatable(fmt, data), true);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		var source = context.getSource();
		if(isError) {
			source.sendFailure(reply);
		} else {
			source.sendSuccess(reply, false);
		}
		return 0;
	}
}