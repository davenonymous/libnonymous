package com.davenonymous.libnonymous.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockProjectionParticleFactory implements IParticleFactory<BlockParticleData> {
    @Nullable
    @Override
    public Particle makeParticle(BlockParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new BlockProjectionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn);
    }
}
