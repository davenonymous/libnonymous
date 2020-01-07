package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.command.CommandOpenConfigGUI;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.function.Supplier;

public class PacketOpenConfigGui {
    private static Field FIELD_MODCONTAINER_CONFIGS;

    static {
        try {
            FIELD_MODCONTAINER_CONFIGS = ModContainer.class.getDeclaredField("configs");
            FIELD_MODCONTAINER_CONFIGS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    protected static EnumMap<ModConfig.Type, ModConfig> getConfigsForModContainer(ModContainer container) {
        try {
            return (EnumMap<ModConfig.Type, ModConfig>) FIELD_MODCONTAINER_CONFIGS.get(container);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    boolean all;
    String modId;
    CommandOpenConfigGUI.Mode mode;

    public PacketOpenConfigGui(boolean all) {
        this.all = true;
        this.mode = CommandOpenConfigGUI.Mode.BY_SPEC;
    }

    public PacketOpenConfigGui(String modId, CommandOpenConfigGUI.Mode mode) {
        this.all = false;
        this.modId = modId;
        this.mode = mode;
    }

    public PacketOpenConfigGui(PacketBuffer buf) {
        this.all = buf.readBoolean();
        if(!this.all) {
            this.modId = buf.readString();
            this.mode = CommandOpenConfigGUI.Mode.values()[buf.readInt()];
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(this.all);
        if(!this.all) {
            buf.writeString(this.modId);
            buf.writeInt(this.mode.ordinal());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            new PacketOpenConfigGuiHandler().accept(this);
        });
        ctx.get().setPacketHandled(true);
    }
}
