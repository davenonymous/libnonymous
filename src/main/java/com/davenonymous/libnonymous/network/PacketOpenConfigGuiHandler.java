package com.davenonymous.libnonymous.network;

import com.davenonymous.libnonymous.command.CommandOpenConfigGUI;
import com.davenonymous.libnonymous.gui.config.WidgetGuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PacketOpenConfigGuiHandler implements Consumer<PacketOpenConfigGui> {
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

    public static void openConfigGuiForAll(Screen parent) {
        List<ForgeConfigSpec> configSpecs = ModList.get().getMods().stream()
                .map(m -> ModList.get().getModContainerById(m.getModId()))
                .filter(m -> m.isPresent())
                .map(m -> getConfigsForModContainer(m.get()))
                .flatMap(m -> m.values().stream())
                .map(m -> m.getSpec())
                .collect(Collectors.toList());

        Minecraft.getInstance().displayGuiScreen(new WidgetGuiConfig(parent, configSpecs));
    }

    @Override
    public void accept(PacketOpenConfigGui packet) {
        if(packet.all) {
            openConfigGuiForAll(null);
        } else {
            Optional<? extends ModContainer> optContainer = ModList.get().getModContainerById(packet.modId);
            if(optContainer.isPresent()) {
                ModContainer modContainer = optContainer.get();
                if(packet.mode == CommandOpenConfigGUI.Mode.NATIVE) {
                    Optional<BiFunction<Minecraft, Screen, Screen>> optExtPoint = modContainer.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY);
                    if(optExtPoint.isPresent()) {
                        BiFunction<Minecraft, Screen, Screen> configGuiFactory = optExtPoint.get();
                        Screen screen = configGuiFactory.apply(Minecraft.getInstance(), null);
                        Minecraft.getInstance().displayGuiScreen(screen);
                    } else {
                        Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("libnonymous.config.error.no_native_menu_exists"), false);
                    }
                } else if(packet.mode == CommandOpenConfigGUI.Mode.BY_SPEC) {
                    EnumMap<ModConfig.Type, ModConfig> configs = getConfigsForModContainer(modContainer);
                    if(configs != null) {
                        List<ForgeConfigSpec> configSpecs = configs.values().stream().map(ModConfig::getSpec).collect(Collectors.toList());
                        Minecraft.getInstance().displayGuiScreen(new WidgetGuiConfig(null, configSpecs));
                    }
                }
            }
        }
    }
}
