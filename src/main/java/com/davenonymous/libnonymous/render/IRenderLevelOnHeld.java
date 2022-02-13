package com.davenonymous.libnonymous.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface IRenderLevelOnHeld {
	void renderEffectOnHeldItem(PoseStack poseStack, Player player, InteractionHand mainHand, float partialTick, Matrix4f projectionMatrix);
}