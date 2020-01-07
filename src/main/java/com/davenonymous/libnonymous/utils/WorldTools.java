package com.davenonymous.libnonymous.utils;

import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class WorldTools {
    public static void foreachBlockBetween(BlockPos min, BlockPos max, Consumer<BlockPos> consumer) {
        int minX = Math.min(min.getX(), max.getX());
        int minY = Math.min(min.getY(), max.getY());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxX = Math.max(min.getX(), max.getX());
        int maxY = Math.max(min.getY(), max.getY());
        int maxZ = Math.max(min.getZ(), max.getZ());

        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    consumer.accept(new BlockPos(x, y, z));
                }
            }
        }
    }
}
