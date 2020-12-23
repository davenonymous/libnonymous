package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.gui.framework.SmartNumberFormatter;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fml.client.gui.GuiUtils;


public class WidgetProgressBar extends WidgetWithValue<Double> {
    int borderColor = 0xFF000000;
    int foregroundColor = 0xFF33AA33;
    int backgroundColor = 0xFF333333;
    int textColor = 0xFFFFFFFF;

    double rangeMin = 0d;
    double rangeMax = 100d;
    EnumDisplayMode displayMode = EnumDisplayMode.PERCENTAGE;

    public WidgetProgressBar() {
        this.value = 0D;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public WidgetProgressBar setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public WidgetProgressBar setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public WidgetProgressBar setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public WidgetProgressBar setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public double getRangeMin() {
        return rangeMin;
    }

    public WidgetProgressBar setRangeMin(double rangeMin) {
        this.rangeMin = rangeMin;
        return this;
    }

    public double getRangeMax() {
        return rangeMax;
    }

    public WidgetProgressBar setRangeMax(double rangeMax) {
        this.rangeMax = rangeMax;
        return this;
    }

    public EnumDisplayMode getDisplayMode() {
        return displayMode;
    }

    public WidgetProgressBar setDisplayMode(EnumDisplayMode displayMode) {
        this.displayMode = displayMode;
        return this;
    }

    @Override
    public void draw(Screen screen) {
        int x = 0;
        int y = 0;
        int width = this.width;
        int height = this.height;

        GuiUtils.drawGradientRect(Matrix4f.makeTranslate(0,0,0), 0, x, y, x+width, y+height, borderColor, borderColor);
        GuiUtils.drawGradientRect(Matrix4f.makeTranslate(0,0,0), 0, x+1, y+1, x+width-1, y+height-1, backgroundColor, backgroundColor );

        double progress = (getValue() - getRangeMin()) / (getRangeMax() - getRangeMin());
        progress = Math.min(Math.max(progress, 0.0d), 1.0d);
        int progressWidth = (int) ((Math.ceil((double)width-2) * progress));

        GuiUtils.drawGradientRect(Matrix4f.makeTranslate(0,0,0), 0, x+1, y+1, x+1+progressWidth, y+height-1, foregroundColor, foregroundColor);

        if(displayMode != EnumDisplayMode.NOTHING && displayMode != EnumDisplayMode.CUSTOM) {
            FontRenderer fr = screen.getMinecraft().fontRenderer;
            String content = "";

            if(displayMode == EnumDisplayMode.PERCENTAGE) {
                content = String.format("%.1f%%", progress*100);
            } else if(displayMode == EnumDisplayMode.VALUE) {
                content = String.valueOf(SmartNumberFormatter.formatNumber(this.getValue()));
            } else if(displayMode == EnumDisplayMode.VALUE_AND_PERCENTAGE) {
                content = String.format("%.1f%% (%s)", progress*100, SmartNumberFormatter.formatNumber(this.getValue()));
            }

            int xPos = x + 1 + (width - fr.getStringWidth(content)) / 2;
            fr.drawString(new MatrixStack(), content, xPos, y+2, textColor);
        }

    }

    public enum EnumDisplayMode {
        NOTHING,
        VALUE,
        PERCENTAGE,
        VALUE_AND_PERCENTAGE,
        CUSTOM
    }
}
