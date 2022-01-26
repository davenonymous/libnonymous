package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.command.ModCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandlers {

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		ModCommands.register(event.getDispatcher());
	}
}