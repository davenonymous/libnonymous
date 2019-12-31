package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.network.Networking;
import net.minecraftforge.common.MinecraftForge;

public class ModSetup {
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        Networking.registerMessages();
    }
}
