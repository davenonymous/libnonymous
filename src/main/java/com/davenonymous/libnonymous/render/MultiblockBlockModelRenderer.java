package com.davenonymous.libnonymous.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.Random;
import java.util.Set;

public class MultiblockBlockModelRenderer {
    private static final Random rand = new Random();

    public static void renderModel(MultiblockBlockModel model) {
        MultiBlockModelWorldReader modelWorld = new MultiBlockModelWorldReader(model);
        renderModel(model, modelWorld);
    }

    public static void renderModel(MultiblockBlockModel model, IEnviromentBlockReader worldContext, BlockPos blockContext) {
        MultiBlockModelWorldReader modelWorld = new MultiBlockModelWorldReader(model, worldContext, blockContext);
        renderModel(model, modelWorld);
    }

    private static void renderModel(MultiblockBlockModel model, MultiBlockModelWorldReader modelWorld) {
        GlStateManager.pushMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

        // Aaaand render
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        GlStateManager.disableAlphaTest();
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.SOLID, modelWorld, model);
        GlStateManager.enableAlphaTest();
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.CUTOUT_MIPPED, modelWorld, model);
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.CUTOUT, modelWorld, model);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.TRANSLUCENT, modelWorld, model);

        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private static void renderLayer(BlockRendererDispatcher brd, BufferBuilder buffer, BlockRenderLayer renderLayer, IEnviromentBlockReader treeWorld, MultiblockBlockModel model) {
        Set<BlockPos> renderPositions = model.getRelevantPositions();

        ForgeHooksClient.setRenderLayer(renderLayer);
        for (BlockPos pos : renderPositions) {
            BlockState state = model.blocks.get(pos);
            if (!state.getBlock().canRenderInLayer(state, renderLayer)) {
                continue;
            }

            try {
                //Logz.info("Rendering: %s", state);
                brd.renderBlock(state, pos, treeWorld, buffer, rand, EmptyModelData.INSTANCE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ForgeHooksClient.setRenderLayer(null);
    }
}
