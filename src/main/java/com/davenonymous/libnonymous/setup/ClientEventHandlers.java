package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.render.IRenderLevelOnHeld;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandlers {

	@SubscribeEvent
	public void onRenderLevel(RenderLevelLastEvent event) {
		boolean hasScreenOpen = Minecraft.getInstance().screen != null;
		if(hasScreenOpen || Minecraft.getInstance().player == null) {
			return;
		}

		Player player = Minecraft.getInstance().player;
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

		if(!mainHand.isEmpty()) {
			if(mainHand.getItem() instanceof IRenderLevelOnHeld renderLevelOnHeld) {
				renderLevelOnHeld.renderEffectOnHeldItem(event.getPoseStack(), player, InteractionHand.MAIN_HAND, event.getPartialTick(), event.getProjectionMatrix());
			} else if(mainHand.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IRenderLevelOnHeld renderLevelOnHeld) {
				renderLevelOnHeld.renderEffectOnHeldItem(event.getPoseStack(), player, InteractionHand.MAIN_HAND, event.getPartialTick(), event.getProjectionMatrix());
			}
		}

		if(!offHand.isEmpty()) {
			if(offHand.getItem() instanceof IRenderLevelOnHeld renderLevelOnHeld) {
				renderLevelOnHeld.renderEffectOnHeldItem(event.getPoseStack(), player, InteractionHand.OFF_HAND, event.getPartialTick(), event.getProjectionMatrix());
			} else if(offHand.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IRenderLevelOnHeld renderLevelOnHeld) {
				renderLevelOnHeld.renderEffectOnHeldItem(event.getPoseStack(), player, InteractionHand.OFF_HAND, event.getPartialTick(), event.getProjectionMatrix());
			}
		}
	}
}