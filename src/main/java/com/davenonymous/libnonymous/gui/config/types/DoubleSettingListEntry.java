package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.ColorHelper;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetInputField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

public class DoubleSettingListEntry extends SettingListEntry {
    WidgetInputField inputField;

    public DoubleSettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super(optionKey, comment, value, defaultValue, columnWidth);
    }

    @Override
    public void fillPanel(int entryHeight) {
        double val = (double) value.get();
        this.setSize(columnWidth, entryHeight+18);

        inputField = new WidgetInputField();
        inputField.setValue(String.format("%.4f", val));
        inputField.setDimensions(5, entryHeight-3, columnWidth - 26, 14);
        this.add(inputField);

        WidgetFontAwesome saveButton = new WidgetFontAwesome(SAVE_ICON, WidgetFontAwesome.IconSize.MEDIUM);
        saveButton.setColor(ColorHelper.COLOR_DISABLED);
        saveButton.setPosition(columnWidth - 16, entryHeight-4);
        saveButton.addListener(MouseEnterEvent.class, (event, widget) -> {
            saveButton.setColor(ColorHelper.COLOR_ENABLED);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        saveButton.addListener(MouseExitEvent.class, (event, widget) -> {
            saveButton.setColor(ColorHelper.COLOR_DISABLED);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        saveButton.addListener(MouseClickEvent.class, (event, widget) -> {
            String inputText = inputField.getText();
            double inputVal = 0.0d;
            try {
                inputVal = Double.parseDouble(inputText);
                value.set(inputVal);
                value.save();
                updateDefaultIconState();
                hideErrorIcon();
            } catch(NumberFormatException e) {
                showErrorIcon(I18n.format("libnonymous.config.error.invalid_decimal"));
            }

            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        this.add(saveButton);
    }

    @Override
    public void setValueInInputField(Object defaultValue) {
        if(defaultValue == null) {
            inputField.setValue("");
        } else {
            inputField.setValue(String.format("%.4f", defaultValue));
        }
    }
}
