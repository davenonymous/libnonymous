package com.davenonymous.libnonymous.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;
import java.util.Set;

public class MultiblockBlockModelRenderer {
    private static final Random rand = new Random();

    public static void renderModel(MultiblockBlockModel model, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        MultiBlockModelWorldReader modelWorld = new MultiBlockModelWorldReader(model);
        renderModel(model, modelWorld, matrix, buffer, light, overlay);
    }

    public static void renderModel(MultiblockBlockModel model, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, IWorldReader worldContext, BlockPos blockContext) {
        MultiBlockModelWorldReader modelWorld = new MultiBlockModelWorldReader(model, worldContext, blockContext);
        renderModel(model, modelWorld, matrix, buffer, light, overlay);
    }

    private static void renderModel(MultiblockBlockModel model, MultiBlockModelWorldReader modelWorld, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();

        // Aaaand render
        Set<BlockPos> renderPositions = model.getRelevantPositions();
        for (BlockPos pos : renderPositions) {
            BlockState state = model.blocks.get(pos);

            try {
                matrix.push();
                matrix.translate(pos.getX(), pos.getY(), pos.getZ());

                if(modelWorld.getContextWorld() != null && modelWorld.getContextPos() != null) {
                    // TODO: Hacks hack hacks. Clean this up, make ambient occlusion work, use proper world references
                    AmbientOcclusionStatus before = Minecraft.getInstance().gameSettings.ambientOcclusionStatus;
                    Minecraft.getInstance().gameSettings.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
                    brd.renderModel(state, modelWorld.getContextPos(), modelWorld.getContextWorld(), matrix, buffer.getBuffer(RenderType.getCutout()), false, rand, EmptyModelData.INSTANCE);
                    Minecraft.getInstance().gameSettings.ambientOcclusionStatus = before;
                } else {
                    brd.renderBlock(state, matrix, buffer, light, overlay, EmptyModelData.INSTANCE);
                }
                matrix.pop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
