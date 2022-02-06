package com.davenonymous.libnonymous.registration;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistrationHelper {
	public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block, DeferredRegister<Item> registry,  Item.Properties ITEM_PROPERTIES) {
		return registry.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
	}
}