package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.render.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient implements IProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(RenderEventHandler.class);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
