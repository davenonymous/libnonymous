package com.davenonymous.libnonymous.reflections;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class BlockColorsReflection {
	private static Field blockColors;

	static {
		blockColors = ObfuscationReflectionHelper.findField(BlockColors.class, "f_92571_");
		blockColors.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public static Map<Reference<Block>, BlockColor> getBlockColorsMaps(BlockColors blockColorsInstance) {
		try {
			return (Map<Reference<Block>, BlockColor>) blockColors.get(blockColorsInstance);
		} catch (IllegalAccessException e) {
		}

		return null;
	}

}