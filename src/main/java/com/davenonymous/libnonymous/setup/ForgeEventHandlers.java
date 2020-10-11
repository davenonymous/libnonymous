package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.command.ModCommands;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class ForgeEventHandlers {
    @SubscribeEvent
    public void serverLoad(FMLServerStartingEvent event) {
        ModCommands.register(event.getServer().getCommandManager().getDispatcher());
    }
}
