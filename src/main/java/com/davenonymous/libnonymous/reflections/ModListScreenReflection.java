package com.davenonymous.libnonymous.reflections;

import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.client.gui.widget.ModListWidget;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModListScreenReflection {
	private static Field modList;
	private static Method displayModConfig;

	static {
		modList = ObfuscationReflectionHelper.findField(ModListScreen.class, "modList");
		modList.setAccessible(true);

		displayModConfig = ObfuscationReflectionHelper.findMethod(ModListScreen.class, "displayModConfig");
		displayModConfig.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public static ModListWidget getModList(ModListScreen modListScreen) {
		try {
			return (ModListWidget) modList.get(modListScreen);
		} catch (IllegalAccessException e) {
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static ModListWidget displayModConfig(ModListScreen modListScreen) {
		try {
			displayModConfig.invoke(modListScreen);
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		return null;
	}
}