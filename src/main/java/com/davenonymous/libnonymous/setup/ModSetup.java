package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.network.Networking;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {
	public static void init(FMLCommonSetupEvent event) {
		Networking.registerMessages();
	}
}