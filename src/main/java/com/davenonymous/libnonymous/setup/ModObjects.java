package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.Libnonymous;
import com.davenonymous.libnonymous.particles.BlockProjectionParticleType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Libnonymous.MODID)
public class ModObjects {
    @ObjectHolder("block_projection_particle")
    public static BlockProjectionParticleType blockProjectionParticleType;
}
