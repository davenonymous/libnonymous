package com.davenonymous.libnonymous.render;

import com.davenonymous.libnonymous.base.BaseBlock;
import com.davenonymous.libnonymous.base.BaseItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderEventHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleRendererForBaseObjects(RenderWorldLastEvent event) {
        if(!Minecraft.isGuiEnabled() || Minecraft.getInstance().player == null) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        ItemStack mainHand = player.getHeldItem(Hand.MAIN_HAND);
        ItemStack offHand = player.getHeldItem(Hand.OFF_HAND);

        if(!mainHand.isEmpty()) {
            if(mainHand.getItem() instanceof BaseItem) {
                BaseItem mainBase = (BaseItem)mainHand.getItem();
                mainBase.renderEffectOnHeldItem(player, Hand.MAIN_HAND, event.getPartialTicks());
            } else if(mainHand.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) mainHand.getItem()).getBlock();
                if(block instanceof BaseBlock) {
                    ((BaseBlock) block).renderEffectOnHeldItem(player, Hand.MAIN_HAND, event.getPartialTicks(), event.getMatrixStack());
                }
            }
        }

        if(!offHand.isEmpty()) {
            if(offHand.getItem() instanceof BaseItem) {
                BaseItem mainBase = (BaseItem)offHand.getItem();
                mainBase.renderEffectOnHeldItem(player, Hand.OFF_HAND, event.getPartialTicks());
            } else if(offHand.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) offHand.getItem()).getBlock();
                if(block instanceof BaseBlock) {
                    ((BaseBlock) block).renderEffectOnHeldItem(player, Hand.OFF_HAND, event.getPartialTicks(), event.getMatrixStack());
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if(event.phase == TickEvent.RenderTickEvent.Phase.START) {
            RenderTickCounter.renderTicks++;
        }
    }
}
