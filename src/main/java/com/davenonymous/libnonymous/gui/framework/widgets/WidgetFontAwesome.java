package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.gui.framework.GUIHelper;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.mojang.blaze3d.platform.GlStateManager;

public class WidgetFontAwesome extends WidgetImage {
    FontAwesomeIcons icon;
    boolean hq;
    boolean small;

    public WidgetFontAwesome(FontAwesomeIcons icon, boolean small, boolean hq) {
        super(icon.get(hq));
        this.icon = icon;
        this.hq = hq;
        this.small = small;

        if(this.hq) {
            this.setTextureSize(icon.getWidth()*2, icon.getHeight()*2);
        } else {
            this.setTextureSize(icon.getWidth(), icon.getHeight());
        }

        if(this.small) {
            this.setSize(icon.getWidth(), icon.getHeight());
        } else {
            this.setSize(icon.getWidth()*2, icon.getHeight()*2);
        }
    }

    public WidgetFontAwesome setIcon(FontAwesomeIcons icon) {
        this.icon = icon;
        return this;
    }

    public int getIconWidth() {
        return this.small ? icon.getWidth() : getIconWidth()*2;
    }

    public int getIconHeight() {
        return this.small ? icon.getHeight() : getIconHeight()*2;
    }

    @Override
    protected void actuallyDraw() {
        if(this.hq) {
            GlStateManager.scaled(0.5d, 0.5d, 0.0d);
            GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width*2, height*2, textureWidth, textureHeight);
        } else {
            GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, textureWidth, textureHeight);
        }
    }
}
