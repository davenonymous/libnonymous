package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.gui.framework.GUI;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.Collections;

public class WidgetItemStack extends WidgetWithValue<ItemStack> {
    boolean drawSlot = false;

    public WidgetItemStack(ItemStack stack) {
        this.setSize(16, 16);
        this.setValue(stack);
    }

    public WidgetItemStack(ItemStack stack, boolean drawSlot) {
        this(stack);
        this.drawSlot = drawSlot;
    }

    public void setValue(ItemStack stack) {
        if(!stack.isEmpty()) {
            ITooltipFlag.TooltipFlags tooltipFlag = Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
            this.setTooltipLines(stack.getTooltip(Minecraft.getInstance().player, tooltipFlag));
        } else {
            this.setTooltipLines(Collections.emptyList());
        }

        super.setValue(stack);
    }

    @Override
    public void draw(Screen screen) {
        super.draw(screen);

        if(drawSlot) {
            this.drawSlot(screen);
        }

        if(this.value == null || this.value.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();
        GlStateManager.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.

        double xScale = this.width / 16.0f;
        double yScale = this.height / 16.0f;

        GlStateManager.scaled(xScale, yScale, 1.0d);

        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.value, 0, 0);

        GlStateManager.popMatrix();
    }

    private void drawSlot(Screen screen) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1f);
        screen.getMinecraft().textureManager.bindTexture(GUI.tabIcons);

        int texOffsetY = 84;
        int texOffsetX = 84;

        GuiUtils.drawTexturedModalRect(-1, -1, texOffsetX, texOffsetY, 18, 18, 0.0f);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
    }
}
