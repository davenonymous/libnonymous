package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.gui.framework.GUIHelper;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.mojang.blaze3d.platform.GlStateManager;

public class WidgetFontAwesome extends WidgetImage {
    FontAwesomeIcons icon;
    IconSize size;

    public WidgetFontAwesome(FontAwesomeIcons icon, IconSize iconSize) {
        super(iconSize == IconSize.TINY ? icon.get(false) : icon.get(true));
        this.icon = icon;
        this.size = iconSize;

        this.setTextureSize(icon.getWidth()*2, icon.getHeight()*2);

        this.setSize(iconSize.getWidgetWidth(icon), iconSize.getWidgetHeight(icon));
    }

    public WidgetFontAwesome setIcon(FontAwesomeIcons icon) {
        this.icon = icon;
        this.image = size == IconSize.TINY ? icon.get(false) : icon.get(true);
        return this;
    }

    public WidgetFontAwesome setSize(IconSize size) {
        this.size = size;
        this.image = size == IconSize.TINY ? icon.get(false) : icon.get(true);
        return this;
    }

    @Override
    protected void actuallyDraw() {
        //if(this.hq) {
        //    GlStateManager.scaled(0.5d, 0.5d, 0.0d);
        //    GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width*2, height*2, textureWidth, textureHeight);
        //} else {
        GlStateManager.scalef(size.sizeModifier, size.sizeModifier, size.sizeModifier);
        GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width*size.sizeFactor, height*size.sizeFactor, textureWidth, textureHeight);
        //}
    }

    public enum IconSize {
        TINY(0.25f, 4),
        MEDIUM(0.5f, 2),
        LARGE(1.0f, 1);

        float sizeModifier;
        int sizeFactor;

        IconSize(float sizeModifier, int sizeFactor) {
            this.sizeModifier = sizeModifier;
            this.sizeFactor = sizeFactor;
        }

        public int getWidgetWidth(FontAwesomeIcons icon) {
            return (int)(icon.getWidth() * sizeModifier * 2);
        }

        public int getWidgetHeight(FontAwesomeIcons icon) {
            return (int)(icon.getHeight() * sizeModifier * 2);
        }
    }
}
