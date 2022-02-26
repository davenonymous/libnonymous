package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.base.BasePacket;
import com.davenonymous.libnonymous.serialization.Sync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			PacketOpenModsGuiWrapper.doIt(preselectMod, openConfigScreen);
		});
	}
}