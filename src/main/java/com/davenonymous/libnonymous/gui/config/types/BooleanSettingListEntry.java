package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import net.minecraftforge.common.ForgeConfigSpec;

public class BooleanSettingListEntry extends SettingListEntry {
    WidgetFontAwesome toggleButtonOn;
    WidgetFontAwesome toggleButtonOff;

    public BooleanSettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super(optionKey, comment, value, defaultValue, columnWidth);
    }

    public void fillPanel(int baseEntryHeight) {
        this.setSize(columnWidth, baseEntryHeight);

        boolean val = (boolean) value.get();
        int buttonX = columnWidth - 16;
        int buttonY = ((baseEntryHeight - 16) / 2) - 2;

        toggleButtonOn = new WidgetFontAwesome(FontAwesomeIcons.REGULAR_CheckCircle, WidgetFontAwesome.IconSize.MEDIUM);
        toggleButtonOn.setColor(COLOR_ENABLED);
        toggleButtonOn.setPosition(buttonX, buttonY);
        this.add(toggleButtonOn);

        toggleButtonOff = new WidgetFontAwesome(FontAwesomeIcons.REGULAR_Circle, WidgetFontAwesome.IconSize.MEDIUM);
        toggleButtonOff.setColor(COLOR_DISABLED);
        toggleButtonOff.setPosition(buttonX, buttonY);
        this.add(toggleButtonOff);

        toggleButtonOff.addListener(MouseClickEvent.class, (event, widget) -> {
            value.set(true);
            value.save();
            updateDefaultIconState();
            toggleButtonOn.setVisible(true);
            toggleButtonOff.setVisible(false);

            //Networking.reloadConfigs();

            return WidgetEventResult.HANDLED;
        });

        toggleButtonOn.addListener(MouseClickEvent.class, (event, widget) -> {
            value.set(false);
            value.save();
            updateDefaultIconState();
            toggleButtonOn.setVisible(false);
            toggleButtonOff.setVisible(true);

            //Networking.reloadConfigs();
            return WidgetEventResult.HANDLED;
        });

        toggleButtonOn.setVisible(val);
        toggleButtonOff.setVisible(!val);
    }

    @Override
    public void setValueInInputField(Object defaultValue) {
        if(defaultValue == null) {
            toggleButtonOn.setVisible(false);
            toggleButtonOff.setVisible(true);
        } else {
            boolean realVal = (boolean)defaultValue;
            toggleButtonOn.setVisible(realVal);
            toggleButtonOff.setVisible(!realVal);
        }
    }
}
