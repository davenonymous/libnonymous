package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.base.BasePacket;
import com.davenonymous.libnonymous.reflections.ModListScreenReflection;
import com.davenonymous.libnonymous.serialization.Sync;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenModsGui extends BasePacket {
	@Sync
	String preselectMod;

	@Sync
	boolean openConfigScreen;

	public PacketOpenModsGui(String mod, boolean openConfigScreen) {
		super();
		this.preselectMod = mod;
		this.openConfigScreen = openConfigScreen;
	}

	public PacketOpenModsGui(FriendlyByteBuf buf) {
		super(buf);
	}

	@Override
	public void doWork(Supplier<NetworkEvent.Context> ctx) {
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