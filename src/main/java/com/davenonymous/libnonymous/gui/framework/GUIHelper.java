package com.davenonymous.libnonymous.gui.framework;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.FontRenderer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GUIHelper {
    public static void drawSplitStringCentered(String str, Screen screen, int x, int y, int width, int color) {
        FontRenderer renderer = screen.getMinecraft().fontRenderer;
        int yOffset = 0;
        for(String row : renderer.listFormattedStringToWidth(str, width)) {
            screen.drawCenteredString(renderer, row, x + width/2, y + yOffset, color);
            yOffset += renderer.FONT_HEIGHT;
        }
    }

    public static void drawColoredRectangle(int x, int y, int width, int height, int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = (argb & 0xFF);
        drawColoredRectangle(x, y, width, height, r, g, b, a);
    }

    public static void drawColoredRectangle(int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        double zLevel = 0.0f;

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((x + 0), (y + 0), zLevel).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + 0), (y + height), zLevel).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y + height), zLevel).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y + 0), zLevel).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();

    }

    public static void drawStretchedTexture(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
        float f =  0.00390625F;
        double zLevel = 0.0f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder
                .pos((double)(x + 0), (double)(y + height), zLevel)
                .tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + textureHeight) * f))
                .endVertex();

        bufferbuilder
                .pos((double)(x + width), (double)(y + height), zLevel)
                .tex((double)((float)(textureX + textureWidth) * f), (double)((float)(textureY + textureHeight) * f))
                .endVertex();

        bufferbuilder
                .pos((double)(x + width), (double)(y + 0), zLevel)
                .tex((double)((float)(textureX + textureWidth) * f), (double)((float)(textureY + 0) * f))
                .endVertex();

        bufferbuilder
                .pos((double)(x + 0), (double)(y + 0), zLevel)
                .tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f))
                .endVertex();

        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
    }
}
