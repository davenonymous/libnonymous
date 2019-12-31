package com.davenonymous.libnonymous.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class PacketClipboard {
    protected String clipboardContent;

    public PacketClipboard(String clipboardContent) {
        this.clipboardContent = clipboardContent;
    }

    public PacketClipboard(PacketBuffer buf) {
        this.clipboardContent = buf.readString();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.clipboardContent);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GLFW.glfwSetClipboardString(Minecraft.getInstance().mainWindow.getHandle(), this.clipboardContent);
        });
        ctx.get().setPacketHandled(true);
    }
}
