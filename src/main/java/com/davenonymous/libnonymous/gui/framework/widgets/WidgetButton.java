package com.davenonymous.libnonymous.gui.framework.widgets;


import com.davenonymous.libnonymous.base.BaseLanguageProvider;
import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.GUIHelper;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.helper.Translatable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;


public class WidgetButton extends Widget {
	public boolean hovered = false;
	public ResourceLocation backgroundTexture;
	public Translatable label;
	public String fixedLabel;

	private WidgetButton() {
		this.setHeight(20);
		this.setWidth(100);
		this.backgroundTexture = GUI.defaultButtonTexture;

		this.addListener(MouseClickEvent.class, ((event, widget) -> {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return WidgetEventResult.CONTINUE_PROCESSING;
		}));

		this.addListener(MouseEnterEvent.class, (event, widget) -> {
			((WidgetButton) widget).hovered = true;
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		this.addListener(MouseExitEvent.class, (event, widget) -> {
			((WidgetButton) widget).hovered = false;
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public WidgetButton(Translatable label) {
		this();
		this.label = label;
	}

	public WidgetButton(String label) {
		this();
		this.fixedLabel = label;
	}

	public WidgetButton setBackgroundTexture(ResourceLocation backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
		return this;
	}

	public WidgetButton setLabel(Translatable label) {
		this.label = label;
		return this;
	}

	public WidgetButton setLabel(String label) {
		this.fixedLabel = label;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		//Logz.info("Width: %d, height: %d", width, height);

		pGuiGraphics.pose().pushPose();
		RenderSystem.enableBlend();
		pGuiGraphics.pose().translate(0f, 0f, 2f);

		// Draw the background
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.setShaderTexture(0, backgroundTexture);
		GUIHelper.drawModalRectWithCustomSizedTexture(pGuiGraphics, 0, 0, 0, 0, width, height, 16.0f, 16.0f);

		RenderSystem.setShaderTexture(0, GUI.tabIcons);

		// Top Left corner
		int texOffsetX = 64;
		int texOffsetY = 84;
		int overlayWidth = 20;

		pGuiGraphics.blit(GUI.tabIcons, 0, 0, texOffsetX, texOffsetY, 4, 4);


		// Top right corner
		pGuiGraphics.blit(GUI.tabIcons, 0 + width - 4, 0, texOffsetX + overlayWidth - 4, texOffsetY, 4, 4);

		// Bottom Left corner
		pGuiGraphics.blit(GUI.tabIcons, 0, this.height - 4, texOffsetX, texOffsetY + overlayWidth - 4, 4, 4);

		// Bottom Right corner
		pGuiGraphics.blit(GUI.tabIcons, 0 + width - 4, this.height - 4, texOffsetX + overlayWidth - 4, texOffsetY + overlayWidth - 4, 4, 4);


		// Top edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0 + 4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 12, 4);

		// Bottom edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0 + 4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + overlayWidth - 4, 12, 4);

		// Left edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0, 4, 4, this.height - 8, texOffsetX, texOffsetY + 4, 4, 12);

		// Right edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0 + width - 4, 4, 4, this.height - 8, texOffsetX + overlayWidth - 4, texOffsetY + 3, 4, 12);

		Font fontrenderer = screen.getMinecraft().font;
		pGuiGraphics.pose().translate(0f, 0f, 10f);
		drawButtonContent(pGuiGraphics, screen, fontrenderer);
		pGuiGraphics.pose().translate(0f, 0f, -10f);

		if(!enabled) {
			GUIHelper.drawColoredRectangle(pGuiGraphics, 1, 1, width - 2, height - 2, 0x80000000);
		} else if(hovered) {
			GUIHelper.drawColoredRectangle(pGuiGraphics, 1, 1, width - 2, height - 2, 0x808090FF);
		}

		pGuiGraphics.pose().popPose();
	}

	protected void drawButtonContent(GuiGraphics pGuiGraphics, Screen screen, Font renderer) {
		drawString(pGuiGraphics, screen, renderer);
	}

	protected void drawString(GuiGraphics pGuiGraphics, Screen screen, Font renderer) {
		int color = 0xFFFFFF;
		String toDraw = fixedLabel != null ? fixedLabel : I18n.get(BaseLanguageProvider.getTranslatableLanguageKey(label));
		pGuiGraphics.drawCenteredString(screen.getMinecraft().font, toDraw, (int)(width / 2.0f), (int)((height - 8) / 2.0f), color);
	}
}