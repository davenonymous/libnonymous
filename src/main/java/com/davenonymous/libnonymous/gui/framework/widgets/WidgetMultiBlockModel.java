package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.render.MultiblockBlockModelRenderer;
import com.davenonymous.libnonymous.render.RenderTickCounter;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;

public class WidgetMultiBlockModel extends Widget {
    private MultiblockBlockModel model;

    public WidgetMultiBlockModel(MultiblockBlockModel model) {
        this.model = model;
    }

    @Override
    public void draw(Screen screen) {
        float angle = RenderTickCounter.renderTicks * 45.0f / 128.0f;

        GlStateManager.pushMatrix();

        // Init GlStateManager
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableAlphaTest();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        GlStateManager.disableRescaleNormal();

        // Bring to front
        GlStateManager.translatef(0F, 0F, 216.5F);

        double scaledWidth = this.width * 1.4d;
        double scaledHeight = this.height * 1.4d;

        GlStateManager.translated(scaledWidth / 2.0f, scaledHeight / 2.0f, 0.0d);

        // Shift it a bit down so one can properly see 3d
        GlStateManager.rotatef(-25.0f, 1.0f, 0.0f, 0.0f);

        // Rotate per our calculated time
        GlStateManager.rotatef(angle, 0.0f, 1.0f, 0.0f);

        double scale = model.getScaleRatio(true);
        GlStateManager.scaled(scale, scale, scale);

        GlStateManager.scaled(scaledWidth, scaledWidth, scaledWidth);


        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);

        GlStateManager.translatef(
                (model.width + 1) / -2.0f,
                (model.height + 1) / -2.0f,
                (model.depth + 1) / -2.0f
        );

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GL11.glFrontFace(GL11.GL_CW);
        MultiblockBlockModelRenderer.renderModel(this.model);

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GL11.glFrontFace(GL11.GL_CCW);

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
