package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.GUIHelper;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.client.gui.GuiUtils;

import static org.lwjgl.opengl.GL11.GL_QUADS;


public class WidgetButton extends Widget {
    protected String unlocalizedLabel;
    public boolean hovered = false;
    public ResourceLocation backgroundTexture;
    public TextureAtlasSprite atlasSprite;

    public WidgetButton(String unlocalizedLabel) {
        this.setHeight(20);
        this.setWidth(100);

        this.unlocalizedLabel = unlocalizedLabel;
        this.backgroundTexture = GUI.defaultButtonTexture;

        this.addListener(MouseClickEvent.class, ((event, widget) -> {
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return WidgetEventResult.CONTINUE_PROCESSING;
        }));

        this.addListener(MouseEnterEvent.class, (event, widget) -> {((WidgetButton)widget).hovered = true; return WidgetEventResult.CONTINUE_PROCESSING; });
        this.addListener(MouseExitEvent.class, (event, widget) -> {((WidgetButton)widget).hovered = false; return WidgetEventResult.CONTINUE_PROCESSING; });
    }

    public WidgetButton setBackgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public WidgetButton setAtlasSprite(TextureAtlasSprite atlasSprite) {
        this.atlasSprite = atlasSprite;
        return this;
    }

    public void setUnlocalizedLabel(String unlocalizedLabel) {
        this.unlocalizedLabel = unlocalizedLabel;
    }

    @Override
    public void draw(Screen screen) {
        //Logz.info("Width: %d, height: %d", width, height);

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.translatef(0.0f, 0.0f, 2.0f);

        // Draw the background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


        if(atlasSprite != null) {
            screen.getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            //screen.drawTexturedModalRect(0, 0, atlasSprite, 16, 16);
            fillAreaWithIcon(atlasSprite, 0, 0, width, height);
            //Gui.drawModalRectWithCustomSizedTexture(0, 0, atlasSprite.getMinU(), atlasSprite.getMinV(), width, height, atlasSprite.getMaxU()-atlasSprite.getMinU(), atlasSprite.getMaxV()-atlasSprite.getMinU());
        } else {
            screen.getMinecraft().getTextureManager().bindTexture(backgroundTexture);
            GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, 16.0f, 16.0f);
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, hovered ? 1.0F : 1.0F);
        screen.getMinecraft().getTextureManager().bindTexture(GUI.tabIcons);

        // Top Left corner
        int texOffsetX = 64;
        int texOffsetY = 84;
        int overlayWidth = 20;

        GuiUtils.drawTexturedModalRect(0, 0, texOffsetX, texOffsetY, 4, 4, 0.0f);


        // Top right corner
        GuiUtils.drawTexturedModalRect(0+width - 4, 0, texOffsetX + overlayWidth - 4, texOffsetY, 4, 4, 0.0f);

        // Bottom Left corner
        GuiUtils.drawTexturedModalRect(0, this.height - 4, texOffsetX, texOffsetY + overlayWidth - 4, 4, 4, 0.0f);

        // Bottom Right corner
        GuiUtils.drawTexturedModalRect(0+width - 4, this.height - 4, texOffsetX + overlayWidth - 4, texOffsetY + overlayWidth - 4, 4, 4, 0.0f);


        // Top edge
        GUIHelper.drawStretchedTexture(0+4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 12, 4);

        // Bottom edge
        GUIHelper.drawStretchedTexture(0+4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + overlayWidth - 4, 12, 4);

        // Left edge
        GUIHelper.drawStretchedTexture(0, 4, 4, this.height - 8, texOffsetX, texOffsetY+4, 4, 12);

        // Right edge
        GUIHelper.drawStretchedTexture(0+width - 4, 4, 4, this.height - 8, texOffsetX + overlayWidth - 4, texOffsetY + 3, 4, 12);

        FontRenderer fontrenderer = screen.getMinecraft().fontRenderer;
        RenderSystem.translatef(0.0f, 0.0f, 10.0f);
        drawButtonContent(screen, fontrenderer);
        RenderSystem.translatef(0.0f, 0.0f, -10.0f);

        if(!enabled) {
            GUIHelper.drawColoredRectangle(1, 1, width-2, height-2, 0x80000000);
        } else if(hovered) {
            GUIHelper.drawColoredRectangle(1, 1, width-2, height-2, 0x808090FF);
        }

        RenderSystem.popMatrix();
    }

    protected void drawButtonContent(Screen screen, FontRenderer renderer) {
        drawString(screen, renderer);
    }

    protected void drawString(Screen screen, FontRenderer renderer) {
        int color = 0xFFFFFF;
        screen.drawCenteredString(renderer, unlocalizedLabel, width / 2, (height - 8) / 2, color);
    }

    /**
     * <p>Fills a specified area on the screen with the provided {@link TextureAtlasSprite}.</p>
     *
     * @param icon   The {@link TextureAtlasSprite} to be displayed
     * @param x      The X coordinate to start drawing from
     * @param y      The Y coordinate to start drawing form
     * @param width  The width of the provided icon to draw on the screen
     * @param height The height of the provided icon to draw on the screen
     */
    public static void fillAreaWithIcon(TextureAtlasSprite icon, int x, int y, int width, int height) {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        b.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        float zLevel = 0.0f;

        int iconWidth = icon.getWidth();
        int iconHeight = icon.getHeight();

        // number of rows & cols of full size icons
        int fullCols = width / iconWidth;
        int fullRows = height / iconHeight;

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        int excessWidth = width % iconWidth;
        int excessHeight = height % iconHeight;

        // interpolated max u/v for the excess row / col
        float partialMaxU = minU + (maxU - minU) * ((float) excessWidth / iconWidth);
        float partialMaxV = minV + (maxV - minV) * ((float) excessHeight / iconHeight);

        int xNow;
        int yNow;
        for (int row = 0; row < fullRows; row++) {
            yNow = y + row * iconHeight;
            for (int col = 0; col < fullCols; col++) {
                // main part, only full icons
                xNow = x + col * iconWidth;
                drawRect(xNow, yNow, iconWidth, iconHeight, zLevel, minU, minV, maxU, maxV);
            }
            if (excessWidth != 0) {
                // last not full width column in every row at the end
                xNow = x + fullCols * iconWidth;
                drawRect(xNow, yNow, iconWidth, iconHeight, zLevel, minU, minV, maxU, maxV);
            }
        }
        if (excessHeight != 0) {
            // last not full height row
            for (int col = 0; col < fullCols; col++) {
                xNow = x + col * iconWidth;
                yNow = y + fullRows * iconHeight;
                drawRect(xNow, yNow, iconWidth, excessHeight, zLevel, minU, minV, maxU, partialMaxV);
            }
            if (excessWidth != 0) {
                // missing quad in the bottom right corner of neither full height nor full width
                xNow = x + fullCols * iconWidth;
                yNow = y + fullRows * iconHeight;
                drawRect(xNow, yNow, excessWidth, excessHeight, zLevel, minU, minV, partialMaxU, partialMaxV);
            }
        }

        t.draw();
    }

    private static void drawRect(float x, float y, float width, float height, float z, float u, float v, float maxU, float maxV) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();

        b.pos(x, y + height, z).tex(u, maxV).endVertex();
        b.pos(x + width, y + height, z).tex(maxU, maxV).endVertex();
        b.pos(x + width, y, z).tex(maxU, v).endVertex();
        b.pos(x, y, z).tex(u, v).endVertex();
    }
}
