package com.davenonymous.libnonymous.reflections;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AbstractTreeGrowerReflection {
	private static Method getConfiguredFeature;

	static {
		getConfiguredFeature = ObfuscationReflectionHelper.findMethod(AbstractTreeGrower.class, "m_6486_", RandomSource.class, boolean.class);
		getConfiguredFeature.setAccessible(true);
	}

	public static ConfiguredFeature<?, ?> getConfiguredFeature(AbstractTreeGrower grower, RandomSource random, boolean pLargeHive) {
		try {
			ConfiguredFeature<?, ?> feature = (ConfiguredFeature<?, ?>) getConfiguredFeature.invoke(grower, random, pLargeHive);
			return feature;
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		return null;
	}
}