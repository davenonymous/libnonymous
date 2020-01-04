package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.gui.framework.WidgetSlot;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketEnabledSlots {
    boolean[] enabledSlots;

    public PacketEnabledSlots(List<Slot> slots) {
        this.enabledSlots = new boolean[slots.size()];

        int index = 0;
        for(Slot slot : slots) {
            this.enabledSlots[index] = slot.isEnabled();
            index++;
        }
    }

    public PacketEnabledSlots(boolean[] enabledSlots) {
        this.enabledSlots = enabledSlots;
    }

    public PacketEnabledSlots(PacketBuffer buf) {
        int count = buf.readInt();
        enabledSlots = new boolean[count];
        for (int i = 0; i < count; i++) {
            enabledSlots[i] = buf.readBoolean();
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(enabledSlots.length);
        for (int i = 0; i < enabledSlots.length; i++) {
            buf.writeBoolean(enabledSlots[i]);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.get().getSender();
            int index = 0;
            for(Slot slot : serverPlayer.openContainer.inventorySlots) {
                if(slot instanceof WidgetSlot) {
                    if(index >= this.enabledSlots.length) {
                        break;
                    }

                    ((WidgetSlot) slot).setEnabled(this.enabledSlots[index]);
                }

                index++;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
