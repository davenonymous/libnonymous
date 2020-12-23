package com.davenonymous.libnonymous.gui.framework.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

public class WidgetCheckbox extends WidgetSelectButton<Boolean> {
    public WidgetCheckbox() {
        this.addChoice(true, false);
        this.setWidth(10);
        this.setHeight(10);

        this.addClickListener();
    }

    @Override
    protected void drawButtonContent(Screen screen, FontRenderer fontrenderer) {
        if(this.getValue()) {
            fontrenderer.drawString(new MatrixStack(), "x", 2.2f, 0.3f, 0xEEEEEE);
        }
    }
}
