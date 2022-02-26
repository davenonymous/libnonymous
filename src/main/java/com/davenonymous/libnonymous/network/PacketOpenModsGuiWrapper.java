package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.reflections.ModListScreenReflection;
import net.minecraft.client.Minecraft;

public class PacketOpenModsGuiWrapper {
	public static void doIt(String preselectMod, boolean openConfigScreen) {
		var screen = new net.minecraftforge.client.gui.ModListScreen(null) {
			@Override
			public void init() {
				super.init();
				if(!"".equals(preselectMod)) {
					var modList = ModListScreenReflection.getModList(this);
					for(var modEntry : modList.children()) {
						var name = modEntry.getInfo().getModId();
						if(preselectMod.equals(name)) {
							this.setSelected(modEntry);
							if(openConfigScreen) {
								ModListScreenReflection.displayModConfig(this);
							}
							break;
						}
					}
				}
			}
		};

		Minecraft.getInstance().setScreen(screen);
	}
}