package com.davenonymous.libnonymous.gui.framework;

import com.davenonymous.libnonymous.Libnonymous;
import com.davenonymous.libnonymous.gui.framework.widgets.IValueProvider;
import com.davenonymous.libnonymous.gui.framework.widgets.Widget;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.HashMap;
import java.util.Map;

public class GUI extends WidgetPanel {
    public static ResourceLocation tabIcons = new ResourceLocation(Libnonymous.MODID, "textures/gui/tabicons.png");

    public boolean hasTabs = false;
    private Map<ResourceLocation, IValueProvider> valueMap = new HashMap<>();

    public GUI(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    public void findValueWidgets() {
        this.findValueWidgets(this);
    }

    public void registerValueWidget(ResourceLocation id, IValueProvider widget) {
        this.valueMap.put(id, widget);
    }

    public Object getValue(ResourceLocation id) {
        if(id == null || !valueMap.containsKey(id)) {
            return null;
        }

        return valueMap.get(id).getValue();
    }

    public void drawGUI(Screen screen) {
        this.setX((screen.width - this.width)/2);
        this.setY((screen.height - this.height)/2);

        this.shiftAndDraw(screen);
    }

    @Override
    public void drawBeforeShift(Screen screen) {
        //screen.drawDefaultBackground();

        super.drawBeforeShift(screen);
    }

    @Override
    public void draw(Screen screen) {
        drawWindow(screen);
        super.draw(screen);
    }

    protected void drawWindow(Screen screen) {
        GlStateManager.disableLighting();

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        screen.getMinecraft().textureManager.bindTexture(tabIcons);

        float offsetX = 0.0f;
        float offsetY = 0.0f;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(offsetX, offsetY, 0);

        int texOffsetY = 11;
        int texOffsetX = 64;

        int width = this.width;
        int xOffset = 0;

        if(hasTabs) {
            width -= 32;
            xOffset += 32;
        }

        // Top Left corner
        GuiUtils.drawTexturedModalRect(xOffset, 0, texOffsetX, texOffsetY, 4, 4, 0.0f);

        // Top right corner
        GuiUtils.drawTexturedModalRect(xOffset+width - 4, 0, texOffsetX + 4 + 64, texOffsetY, 4, 4, 0.0f);

        // Bottom Left corner
        GuiUtils.drawTexturedModalRect(xOffset, this.height - 4, texOffsetX, texOffsetY + 4 + 64, 4, 4, 0.0f);

        // Bottom Right corner
        GuiUtils.drawTexturedModalRect(xOffset+width - 4, this.height - 4, texOffsetX + 4 + 64, texOffsetY + 4 + 64, 4, 4, 0.0f);

        // Top edge
        GUIHelper.drawStretchedTexture(xOffset+4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 64, 4);

        // Bottom edge
        GUIHelper.drawStretchedTexture(xOffset+4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + 4 + 64, 64, 4);

        // Left edge
        GUIHelper.drawStretchedTexture(xOffset, 4, 4, this.height - 8, texOffsetX, texOffsetY+4, 4, 64);

        // Right edge
        GUIHelper.drawStretchedTexture(xOffset+width - 4, 4, 4, this.height - 8, texOffsetX + 64 + 4, texOffsetY + 3, 4, 64);

        GUIHelper.drawStretchedTexture(xOffset+4, 4, width - 8, this.height - 8, texOffsetX + 4, texOffsetY+4, 64, 64);

        GlStateManager.popMatrix();
    }

    public void drawTooltips(Screen screen, int mouseX, int mouseY) {
        Widget hoveredWidget = getHoveredWidget(mouseX, mouseY);
        FontRenderer font = screen.getMinecraft().fontRenderer;

        if(hoveredWidget != null && hoveredWidget.getTooltip() != null) {
            if(hoveredWidget.getTooltip().size() > 0) {
                GuiUtils.drawHoveringText(hoveredWidget.getTooltipAsString(), mouseX, mouseY, width, height, 180, font);
            }/* else {
                List<String> tooltips = new ArrayList<>();
                tooltips.add(hoveredWidget.toString());
                GuiUtils.drawHoveringText(tooltips, mouseX, mouseY, width, height, 180, font);
            }*/
        }
    }

    public void drawSlot(Screen screen, Slot slot, int guiLeft, int guiTop) {
        //Logz.info("Drawing slot at %dx%d", slot.xPos, slot.yPos);

        RenderHelper.disableStandardItemLighting();

        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1f);

        /*
        if(slot instanceof WidgetSlot) {
            if(!slot.canTakeStack(screen.getMinecraft().player)) {
                GlStateManager.color4f(1.0f, 0.3f, 0.3f, 1f);
            }
        }
        */

        screen.getMinecraft().textureManager.bindTexture(tabIcons);

        float offsetX = guiLeft-1;
        float offsetY = guiTop-1;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(offsetX, offsetY, 0.0f);

        int texOffsetY = 84;
        int texOffsetX = 84;

        // Top Left corner

        GuiUtils.drawTexturedModalRect(slot.xPos, slot.yPos, texOffsetX, texOffsetY, 18, 18, 0.0f);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
    }
}
