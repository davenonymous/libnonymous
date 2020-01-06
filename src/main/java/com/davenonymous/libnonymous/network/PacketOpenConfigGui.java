package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.command.CommandOpenConfigGUI;
import com.davenonymous.libnonymous.gui.config.WidgetGuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private static EnumMap<ModConfig.Type, ModConfig> getConfigsForModContainer(ModContainer container) {
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
            if(this.all) {
                List<ForgeConfigSpec> configSpecs = ModList.get().getMods().stream()
                        .map(m -> ModList.get().getModContainerById(m.getModId()))
                        .filter(m -> m.isPresent())
                        .map(m -> getConfigsForModContainer(m.get()))
                        .flatMap(m -> m.values().stream())
                        .map(m -> m.getSpec())
                        .collect(Collectors.toList());

                Minecraft.getInstance().displayGuiScreen(new WidgetGuiConfig(null, configSpecs));
            } else {
                Optional<? extends ModContainer> optContainer = ModList.get().getModContainerById(this.modId);
                if(optContainer.isPresent()) {
                    ModContainer modContainer = optContainer.get();
                    if(this.mode == CommandOpenConfigGUI.Mode.NATIVE) {
                        Optional<BiFunction<Minecraft, Screen, Screen>> optExtPoint = modContainer.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY);
                        if(optExtPoint.isPresent()) {
                            BiFunction<Minecraft, Screen, Screen> configGuiFactory = optExtPoint.get();
                            Screen screen = configGuiFactory.apply(Minecraft.getInstance(), null);
                            Minecraft.getInstance().displayGuiScreen(screen);
                        } else {
                            Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("libnonymous.config.error.no_native_menu_exists"), false);
                        }
                    } else if(this.mode == CommandOpenConfigGUI.Mode.BY_SPEC) {
                        EnumMap<ModConfig.Type, ModConfig> configs = getConfigsForModContainer(modContainer);
                        if(configs != null) {
                            List<ForgeConfigSpec> configSpecs = configs.values().stream().map(ModConfig::getSpec).collect(Collectors.toList());
                            Minecraft.getInstance().displayGuiScreen(new WidgetGuiConfig(null, configSpecs));
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
