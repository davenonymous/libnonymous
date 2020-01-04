package com.davenonymous.libnonymous.gui.framework.widgets;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.opengl.GL11;

public class WidgetTextBox extends Widget {
    private String text;
    private int textColor = 0xFFFFFF;

    public WidgetTextBox(String text) {
        this.text = text;
        this.setWidth(100);
        this.setHeight(9);
    }

    public WidgetTextBox(String text, int textColor) {
        this.text = text;
        this.textColor = textColor;
        this.setWidth(100);
        this.setHeight(9);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void draw(Screen screen) {
        if(text == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        int scale = computeGuiScale(screen.getMinecraft());

        int bottomOffset = ((screen.getMinecraft().mainWindow.getHeight()/scale) - (getActualY() + height)) * scale;
        GL11.glScissor(getActualX() * scale, bottomOffset, width*scale, height*scale);

        screen.getMinecraft().fontRenderer.drawSplitString(text, 0, 0, width, textColor);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }
}
