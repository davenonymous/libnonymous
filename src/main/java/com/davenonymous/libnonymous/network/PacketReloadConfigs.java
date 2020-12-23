package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.base.BasePacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketReloadConfigs extends BasePacket {

    public PacketReloadConfigs() {
        super();
    }

    public PacketReloadConfigs(PacketBuffer buf) {
        super(buf);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        //TODO
        //ctx.get().getSender().server.reload();
    }
}
