package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.Libnonymous;
import com.davenonymous.libnonymous.helper.RedstoneMode;
import com.davenonymous.libnonymous.serialization.MultiblockBlockModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Optional;

public class Networking {
	public static SimpleChannel INSTANCE;
	private static final String CHANNEL_NAME = "channel";
	private static int ID = 0;

	public static int nextID() {
		return ID++;
	}

	public static void registerMessages() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Libnonymous.MODID, CHANNEL_NAME), () -> "1.0", s -> true, s -> true);

		INSTANCE.registerMessage(nextID(), PacketEnabledSlots.class, PacketEnabledSlots::toBytes, PacketEnabledSlots::new, PacketEnabledSlots::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

		INSTANCE.registerMessage(nextID(), PacketOpenModsGui.class, PacketOpenModsGui::toBytes, PacketOpenModsGui::new, PacketOpenModsGui::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static void sendEnabledSlotsMessage(List<Slot> inventorySlots) {
		INSTANCE.sendToServer(new PacketEnabledSlots(inventorySlots));
	}

	public static void sendOpenModsGUI(Connection target, String mod, boolean openConfigScreen) {
		INSTANCE.sendTo(new PacketOpenModsGui(mod, openConfigScreen), target, NetworkDirection.PLAY_TO_CLIENT);
	}
}