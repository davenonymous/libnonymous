package com.davenonymous.libnonymous.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class BlockProjectionParticle extends Particle {
    BlockParticleData data;

    public BlockProjectionParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, BlockParticleData data) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);

        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        this.data = data;
        this.maxAge = 200;
        this.canCollide = false;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void renderParticle(BufferBuilder bufferIn, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        GlStateManager.pushMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();


        float x = (float)(prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float y = (float)(prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float z = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        GlStateManager.translated(x, y+0.01d, z);

        GlStateManager.scaled(0.98f, 0.98f, 0.98f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.2f);
        // Aaaand render
        GlStateManager.enableBlend();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        GlStateManager.disableAlphaTest();
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.SOLID, this.world, data.getBlockState(), this.world.rand);
        GlStateManager.enableAlphaTest();
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.CUTOUT_MIPPED, this.world, data.getBlockState(), this.world.rand);
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.CUTOUT, this.world, data.getBlockState(), this.world.rand);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.TRANSLUCENT, this.world, data.getBlockState(), this.world.rand);

        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private static void renderLayer(BlockRendererDispatcher brd, BufferBuilder buffer, BlockRenderLayer renderLayer, IEnviromentBlockReader treeWorld, BlockState state, Random rand) {
        if (!state.getBlock().canRenderInLayer(state, renderLayer)) {
            return;
        }

        ForgeHooksClient.setRenderLayer(renderLayer);
        try {

            brd.renderBlock(state, new BlockPos(0, 0, 0), treeWorld, buffer, rand, EmptyModelData.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ForgeHooksClient.setRenderLayer(null);

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }
}
