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

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PacketOpenConfigGuiHandler implements Consumer<PacketOpenConfigGui> {

    @Override
    public void accept(PacketOpenConfigGui packet) {
        if(packet.all) {
            List<ForgeConfigSpec> configSpecs = ModList.get().getMods().stream()
                    .map(m -> ModList.get().getModContainerById(m.getModId()))
                    .filter(m -> m.isPresent())
                    .map(m -> PacketOpenConfigGui.getConfigsForModContainer(m.get()))
                    .flatMap(m -> m.values().stream())
                    .map(m -> m.getSpec())
                    .collect(Collectors.toList());

            Minecraft.getInstance().displayGuiScreen(new WidgetGuiConfig(null, configSpecs));
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
                    EnumMap<ModConfig.Type, ModConfig> configs = PacketOpenConfigGui.getConfigsForModContainer(modContainer);
                    if(configs != null) {
                        List<ForgeConfigSpec> configSpecs = configs.values().stream().map(ModConfig::getSpec).collect(Collectors.toList());
                        Minecraft.getInstance().displayGuiScreen(new WidgetGuiConfig(null, configSpecs));
                    }
                }
            }
        }
    }
}
