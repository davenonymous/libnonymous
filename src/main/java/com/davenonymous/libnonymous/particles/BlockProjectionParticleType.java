package com.davenonymous.libnonymous.particles;

import com.davenonymous.libnonymous.setup.ModObjects;
import net.minecraft.block.BlockState;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockProjectionParticleType extends ParticleType<BlockParticleData> {
    public BlockProjectionParticleType(boolean alwaysShow) {
        super(alwaysShow, BlockParticleData.DESERIALIZER);
    }

    public static void spawn(ServerWorld world, BlockPos pos, BlockState state) {
        world.spawnParticle(new BlockParticleData(ModObjects.blockProjectionParticleType, state), pos.getX(), pos.getY(), pos.getZ(), 1, 0 ,0, 0, 0 );
    }
}
