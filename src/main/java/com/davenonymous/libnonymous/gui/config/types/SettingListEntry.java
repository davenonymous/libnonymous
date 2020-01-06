package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetListEntry;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.utils.FontAwesomeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.awt.*;


public abstract class SettingListEntry extends WidgetListEntry {
    public static final FontAwesomeIcons SAVE_ICON = FontAwesomeIcons.REGULAR_Save;

    protected int columnWidth;

    protected static Color COLOR_ENABLED = new Color(50, 125, 50);
    protected static Color COLOR_DISABLED = new Color(160, 160, 160, 255);
    protected static Color COLOR_ERRORED = new Color(150, 50, 50);

    protected ForgeConfigSpec.ConfigValue value;
    protected Object defaultValue;

    protected WidgetTextBox label;
    protected WidgetTextBox labelComment;
    protected WidgetFontAwesome errorIcon;
    protected WidgetFontAwesome restoreDefaultIcon;

    public SettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super();
        this.columnWidth = columnWidth;
        this.value = value;
        this.defaultValue = defaultValue;

        int availableSpaceForText = columnWidth - 30;

        label = new WidgetTextBox(optionKey, 0xA0FFFFFF);
        label.setDimensions(2, 2, availableSpaceForText, 9);
        this.add(label);

        if(comment == null) {
            comment = "";
        }

        String trimmedComment = Minecraft.getInstance().fontRenderer.trimStringToWidth(comment, availableSpaceForText-13);
        if(trimmedComment.length() < comment.length()) {
            trimmedComment += "...";
        }

        int headerHeight = 14;
        int commentHeight = 9;
        if(trimmedComment.length() <= 0) {
            commentHeight = 0;
        }

        labelComment = new WidgetTextBox(trimmedComment, 0xA0CCCCCC);
        labelComment.setDimensions(2, 13, availableSpaceForText, commentHeight);
        this.add(labelComment);

        if(comment.length() > 0) {
            label.setTooltipLines(new StringTextComponent(comment));
            labelComment.setTooltipLines(new StringTextComponent(comment));
        }

        this.setSize(columnWidth, 45);

        errorIcon = new WidgetFontAwesome(FontAwesomeIcons.SOLID_ExclamationTriangle, WidgetFontAwesome.IconSize.MEDIUM);
        errorIcon.setPosition(columnWidth-18, 2);
        errorIcon.setColor(COLOR_ERRORED);
        errorIcon.setVisible(false);
        this.add(errorIcon);

        this.fillPanel(headerHeight + 5 + commentHeight);
    }

    protected void showErrorIcon(String message) {
        errorIcon.setTooltipLines(new StringTextComponent(message));
        errorIcon.setVisible(true);
    }

    protected void hideErrorIcon() {
        errorIcon.setVisible(false);
    }

    protected void addUnsupportedRow(int entryHeight) {
        this.setSize(columnWidth, entryHeight+17);

        WidgetTextBox textBox = new WidgetTextBox("Sorry, this config type is not supported.", COLOR_ERRORED.getRGB());
        textBox.setSize(columnWidth-10, 9);
        textBox.setPosition(25, 30);
        this.add(textBox);

        WidgetFontAwesome sadIcon = new WidgetFontAwesome(FontAwesomeHelper.getRandomAnimalIcon(), WidgetFontAwesome.IconSize.MEDIUM);
        sadIcon.setColor(COLOR_ERRORED);
        sadIcon.setPosition(6, 24);
        this.add(sadIcon);
    }

    public abstract void fillPanel(int entryHeight);
}