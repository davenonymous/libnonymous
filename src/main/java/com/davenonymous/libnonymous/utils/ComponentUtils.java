package com.davenonymous.libnonymous.utils;

import net.minecraft.network.chat.Component;

public class ComponentUtils {
	public static Component format(String fmt, Object... data) {
		return Component.literal(String.format(fmt, data));
	}
}