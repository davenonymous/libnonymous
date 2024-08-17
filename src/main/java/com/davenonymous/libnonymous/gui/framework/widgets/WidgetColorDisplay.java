package com.davenonymous.libnonymous.gui.framework.widgets;


import com.davenonymous.libnonymous.gui.framework.GUIHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class WidgetColorDisplay extends Widget {
	private Color colorA;
	private Color colorB;
	private boolean horizontal;

	public WidgetColorDisplay(Color color) {
		this.colorA = color;
		this.colorB = color;
		this.horizontal = false;
	}

	public Color getColor() {
		return colorA;
	}

	public WidgetColorDisplay setColor(Color color) {
		this.colorA = color;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		GUIHelper.drawColoredRectangle(pGuiGraphics, 0, 0, width, height, colorA.getRGB());
	}

}