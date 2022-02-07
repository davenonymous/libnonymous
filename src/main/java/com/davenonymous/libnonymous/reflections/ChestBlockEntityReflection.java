package com.davenonymous.libnonymous.reflections;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class ChestBlockEntityReflection {
	private static Field lootTable;

	static {
		lootTable = ObfuscationReflectionHelper.findField(RandomizableContainerBlockEntity.class, "f_59605_");
		lootTable.setAccessible(true);
	}

	public static void removeLootTableFromChest(RandomizableContainerBlockEntity chestEntity) {
		try {
			lootTable.set(chestEntity, null);
		} catch (IllegalAccessException e) {
		}
	}

	@SuppressWarnings("unchecked")
	public static ResourceLocation getLootTableFromChest(RandomizableContainerBlockEntity chestEntity) {
		try {
			return (ResourceLocation) lootTable.get(chestEntity);
		} catch (IllegalAccessException e) {
		}

		return null;
	}
}