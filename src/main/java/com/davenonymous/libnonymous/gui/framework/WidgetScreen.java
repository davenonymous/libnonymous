package com.davenonymous.libnonymous.gui.framework;

import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseMoveEvent;
import com.davenonymous.libnonymous.gui.framework.event.UpdateScreenEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class WidgetScreen extends Screen {
    protected GUI gui;
    private int previousMouseX = Integer.MAX_VALUE;
    private int previousMouseY = Integer.MAX_VALUE;

    protected ResourceLocation id;

    protected WidgetScreen(ITextComponent title) {
        super(title);

        this.gui = createGUI();
        this.gui.setVisible(true);
    }

    protected abstract GUI createGUI();

    @Override
    public void tick() {
        super.tick();
        gui.fireEvent(new UpdateScreenEvent());
        this.resetMousePositions();
    }

    // TODO: Keyboard and mouse events need to be reworked


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(gui.fireEvent(new MouseClickEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if(mouseX != previousMouseX || mouseY != previousMouseY) {
            gui.fireEvent(new MouseMoveEvent(mouseX, mouseY));

            previousMouseX = mouseX;
            previousMouseY = mouseY;
        }

        RenderHelper.enableGUIStandardItemLighting();
        gui.drawGUI(this);
        gui.drawTooltips(this, mouseX, mouseY);
        //renderHoveredToolTip(mouseX, mouseY);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        // Draw default background?
    }

    protected void resetMousePositions() {
        this.previousMouseX = Integer.MIN_VALUE;
        this.previousMouseY = Integer.MIN_VALUE;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
