package com.davenonymous.libnonymous.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketReloadConfigs {

    public PacketReloadConfigs() {
    }

    public PacketReloadConfigs(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ctx.get().getSender().server.reload();
        });
        ctx.get().setPacketHandled(true);
    }
}
