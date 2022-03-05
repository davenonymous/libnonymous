package com.davenonymous.libnonymous.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class InventoryHelper {

	public static List<ItemStack> getStacks(ItemStackHandler itemStackHandler) {
		List<ItemStack> result = new ArrayList<>();
		for(int slotNum = 0; slotNum < itemStackHandler.getSlots(); slotNum++) {
			var stack = itemStackHandler.getStackInSlot(slotNum);
			if(stack.isEmpty()) {
				continue;
			}
			result.add(stack);
		}
		return result;
	}
}