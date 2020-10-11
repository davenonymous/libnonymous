package com.davenonymous.libnonymous.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RaytraceHelper {
    public static BlockRayTraceResult rayTrace(World world, Entity entity) {
        return rayTrace(world, entity, 16.0f);
    }

    public static BlockRayTraceResult rayTrace(World world, Entity entity, double blockReachDistance) {
        return rayTrace(world, entity, blockReachDistance, 1.0f);
    }

    @Nullable
    public static BlockRayTraceResult rayTrace(World world, Entity entity, double blockReachDistance, float partialTicks) {
        return rayTrace(world, entity, blockReachDistance, partialTicks, true);
    }

    @Nullable
    public static BlockRayTraceResult rayTrace(World world, Entity entity, double blockReachDistance, float partialTicks, boolean outline) {
        Vector3d vec3d = entity.getEyePosition(partialTicks);
        Vector3d vec3d1 = entity.getLook(partialTicks);
        Vector3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, outline ? RayTraceContext.BlockMode.OUTLINE : RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
    }
}
