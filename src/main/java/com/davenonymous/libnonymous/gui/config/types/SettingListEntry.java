package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.ColorHelper;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetListEntry;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.utils.FontAwesomeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;


public abstract class SettingListEntry extends WidgetListEntry {
    public static final FontAwesomeIcons SAVE_ICON = FontAwesomeIcons.REGULAR_Save;

    protected int columnWidth;

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

        int optionKeyStringWidth = Minecraft.getInstance().fontRenderer.getStringWidth(optionKey);
        label = new WidgetTextBox(optionKey, 0xA0FFFFFF);
        label.setDimensions(2, 2, optionKeyStringWidth+1, 9);
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
        errorIcon.setColor(ColorHelper.COLOR_ERRORED);
        errorIcon.setVisible(false);
        this.add(errorIcon);

        restoreDefaultIcon = new WidgetFontAwesome(FontAwesomeIcons.SOLID_UndoAlt, WidgetFontAwesome.IconSize.TINY);
        restoreDefaultIcon.setColor(ColorHelper.COLOR_DISABLED);
        restoreDefaultIcon.setPosition(optionKeyStringWidth + 5, 2);
        restoreDefaultIcon.setTooltipLines(Arrays.asList(
                new TranslationTextComponent("libnonymous.config.gui.tooltip.default_is"),
                new StringTextComponent(defaultValue != null ? defaultValue.toString() : "")
        ));
        restoreDefaultIcon.addListener(MouseClickEvent.class, (event, widget) -> {
            value.set(defaultValue);
            value.save();
            updateDefaultIconState();
            this.setValueInInputField(defaultValue);
            return WidgetEventResult.HANDLED;
        });
        updateDefaultIconState();

        this.add(restoreDefaultIcon);

        this.fillPanel(headerHeight + 5 + commentHeight);
    }

    protected void updateDefaultIconState() {
        if(value.get() == null) {
            return;
        }

        if(value.get().equals(defaultValue)) {
            restoreDefaultIcon.setColor(ColorHelper.COLOR_DISABLED);
        } else {
            restoreDefaultIcon.setColor(ColorHelper.COLOR_ENABLED);
        }
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

        WidgetTextBox textBox = new WidgetTextBox(I18n.format("libnonymous.config.error.unsupported_config_type"), ColorHelper.COLOR_ERRORED.getRGB());
        textBox.setSize(columnWidth-10, 9);
        textBox.setPosition(25, 30);
        this.add(textBox);

        WidgetFontAwesome sadIcon = new WidgetFontAwesome(FontAwesomeHelper.getRandomAnimalIcon(), WidgetFontAwesome.IconSize.MEDIUM);
        sadIcon.setColor(ColorHelper.COLOR_ERRORED);
        sadIcon.setPosition(6, 24);
        this.add(sadIcon);
    }

    public abstract void setValueInInputField(Object defaultValue);
    public abstract void fillPanel(int entryHeight);
}