package com.davenonymous.libnonymous.utils;

import com.davenonymous.libnonymous.Libnonymous;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class RotationTools {
    @OnlyIn(Dist.CLIENT)
    private static ResourceLocation arrowImage;

    @OnlyIn(Dist.CLIENT)
    public static void renderArrowOnGround(Vec3d hitPosition, BlockPos drawPosition, float partialTicks) {
        Direction facing = RotationTools.getFacingByTriangle(hitPosition);

        RotationTools.TextureRotationList rotList = new RotationTools.TextureRotationList();
        switch (facing) {
            case SOUTH:
                break;
            case WEST:
                rotList.rotateFromStart();
                break;
            case NORTH:
                rotList.rotateFromStart();
                rotList.rotateFromStart();
                break;
            case EAST:
                rotList.rotateFromStart();
                rotList.rotateFromStart();
                rotList.rotateFromStart();
                break;
        }

        if(arrowImage == null) {
             arrowImage = new ResourceLocation(Libnonymous.MODID, "textures/particles/blockmarker.png");
        }
        Minecraft.getInstance().getTextureManager().bindTexture(arrowImage);

        GlStateManager.pushMatrix();

        GlStateManager.translated(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.translatef(drawPosition.getX(), drawPosition.getY(), drawPosition.getZ());

        // Draw with 50% transparency
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);

        // Actually draw
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        rotList.fillBufferBuilder(bufferbuilder, 0.0005d);

        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public static Direction getFacingForPlayer(World world, PlayerEntity player) {
        float blockReachDistance = 6.0F;

        BlockRayTraceResult trace = RaytraceHelper.rayTrace(world, player, blockReachDistance);
        if(trace == null) {
            return null;
        }

        Vec3d hitPosition = trace.getHitVec();

        hitPosition = hitPosition.subtract(new Vec3d(trace.getPos()));
        hitPosition = hitPosition.subtract(0.5d, 0.5d, 0.5d);
        return getFacingByTriangle(hitPosition);
    }

    public static Direction getFacingByTriangle(Vec3d vec) {
        if(vec.z > 0) {
            if(vec.x < 0) {
                // Quadrant 1
                if(Math.abs(vec.x) < Math.abs(vec.z)) {
                    // Bottom
                    return Direction.SOUTH;
                } else {
                    // Left
                    return Direction.WEST;
                }
            } else {
                // Quadrant 2
                if(Math.abs(vec.x) > Math.abs(vec.z)) {
                    // Right
                    return Direction.EAST;
                } else {
                    // Bottom
                    return Direction.SOUTH;
                }
            }
        } else {
            if(vec.x < 0) {
                // Quadrant 3
                if(Math.abs(vec.x) < Math.abs(vec.z)) {
                    // Top
                    return Direction.NORTH;
                } else {
                    // Left
                    return Direction.WEST;
                }

            } else {
                // Quadrant 4
                if(Math.abs(vec.x) > Math.abs(vec.z)) {
                    // Right
                    return Direction.EAST;
                } else {
                    // Top
                    return Direction.NORTH;
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TextureRotationList extends RotatingList<Tuple<Integer, Integer>> {
        public TextureRotationList() {
            this.add(new Tuple<>(0, 1));
            this.add(new Tuple<>(1, 1));
            this.add(new Tuple<>(1, 0));
            this.add(new Tuple<>(0, 0));
        }

        public void fillBufferBuilder(BufferBuilder buffer, double yLevel) {
            buffer.pos(0, yLevel, 1).tex(this.get(0).getA(), this.get(0).getB()).endVertex();
            buffer.pos(1, yLevel, 1).tex(this.get(1).getA(), this.get(1).getB()).endVertex();
            buffer.pos(1, yLevel, 0).tex(this.get(2).getA(), this.get(2).getB()).endVertex();
            buffer.pos(0, yLevel, 0).tex(this.get(3).getA(), this.get(3).getB()).endVertex();

        }
    }


    public static class RotatingList<T> extends ArrayList<T> {
        public void rotateFromStart() {
            T firstElement = this.get(0);
            this.remove(0);

            this.add(firstElement);
        }

        public void rotateFromEnd() {
            T lastElement = this.get(this.size()-1);
            this.remove(this.size()-1);
            this.add(0, lastElement);
        }
    }
}
