package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.gui.framework.GUIHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class WidgetImage extends Widget {
    ResourceLocation image;
    float textureWidth = 16.0f;
    float textureHeight = 16.0f;
    Color color;

    public WidgetImage(ResourceLocation image) {
        this.image = image;
    }

    public WidgetImage setTextureSize(float width, float height) {
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    public WidgetImage setColor(Color color) {
        this.color = color;
        return this;
    }

    public WidgetImage resetColor() {
        this.color = null;
        return this;
    }

    public void draw(Screen screen) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.translatef(0.0f, 0.0f, 2.0f);

        screen.getMinecraft().getTextureManager().bindTexture(image);

        // Draw the image
        if(color == null) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            float[] pColors = color.getRGBColorComponents(null);
            float pA = 1.0f;
            float pR = pColors[0];
            float pG = pColors[1];
            float pB = pColors[2];
            GlStateManager.color4f(pR, pG, pB, pA);
        }

        actuallyDraw();

        //GlStateManager.clearCurrentColor();
        GlStateManager.disableBlend();
        GlStateManager.disableAlphaTest();

        GlStateManager.popMatrix();
    }

    protected void actuallyDraw() {
        GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width*2, height*2, textureWidth, textureHeight);
    }
}
