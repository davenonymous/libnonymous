package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.Libnonymous;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;

public class Networking {
    public static SimpleChannel INSTANCE;
    private static final String CHANNEL_NAME = "channel";
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Libnonymous.MODID, CHANNEL_NAME), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), PacketClipboard.class, PacketClipboard::toBytes, PacketClipboard::new, PacketClipboard::handle);
        INSTANCE.registerMessage(nextID(), PacketEnabledSlots.class, PacketEnabledSlots::toBytes, PacketEnabledSlots::new, PacketEnabledSlots::handle);
    }

    public static void sendClipboardMessage(ServerPlayerEntity to, String clipboard) {
        INSTANCE.sendTo(new PacketClipboard(clipboard), to.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendEnabledSlotsMessage(List<Slot> inventorySlots) {
        INSTANCE.sendToServer(new PacketEnabledSlots(inventorySlots));
    }
}
