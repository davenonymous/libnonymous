package com.davenonymous.libnonymous.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

    void init();

    World getClientWorld();

    PlayerEntity getClientPlayer();
}