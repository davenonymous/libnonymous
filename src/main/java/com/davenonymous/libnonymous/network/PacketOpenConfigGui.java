package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.base.BasePacket;
import com.davenonymous.libnonymous.command.CommandOpenConfigGUI;
import com.davenonymous.libnonymous.serialization.Sync;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenConfigGui extends BasePacket {
    @Sync
    boolean all;

    @Sync
    String modId;

    @Sync
    CommandOpenConfigGUI.Mode mode;

    public PacketOpenConfigGui(boolean all) {
        super();
        this.all = true;
        this.modId = "";
        this.mode = CommandOpenConfigGUI.Mode.BY_SPEC;
    }

    public PacketOpenConfigGui(String modId, CommandOpenConfigGUI.Mode mode) {
        super();
        this.all = false;
        this.modId = modId;
        this.mode = mode;
    }

    public PacketOpenConfigGui(PacketBuffer buf) {
        super(buf);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        new PacketOpenConfigGuiHandler().accept(this);
    }
}
