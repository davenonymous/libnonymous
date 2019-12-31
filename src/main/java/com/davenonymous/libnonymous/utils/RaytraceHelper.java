package com.davenonymous.libnonymous.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RaytraceHelper {
    @Nullable
    public static BlockRayTraceResult rayTrace(World world, Entity entity, double blockReachDistance, float partialTicks) {
        Vec3d vec3d = entity.getEyePosition(0);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return world.rayTraceBlocks(vec3d, vec3d1, new BlockPos(vec3d2), VoxelShapes.empty(), null);
    }
}
