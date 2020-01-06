package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetInputField;
import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnumSettingListEntry extends SettingListEntry {
    public EnumSettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super(optionKey, comment, value, defaultValue, columnWidth);
    }

    @Override
    public void fillPanel(int entryHeight) {
        Class enumClazz;
        if(value.get() != null) {
            enumClazz = value.get().getClass();
        } else if(defaultValue != null) {
            enumClazz = defaultValue.getClass();
        } else {
            this.addUnsupportedRow(entryHeight);
            Logz.warn("Enum values without default setting or empty value are not supported. We don't know the type otherwise!");
            return;
        }

        String val = value.get().toString();
        this.setSize(columnWidth, entryHeight+18);

        WidgetInputField inputField = new WidgetInputField();
        inputField.setValue(val);
        inputField.setDimensions(5, entryHeight-3, columnWidth - 26, 14);
        this.add(inputField);

        WidgetFontAwesome save = new WidgetFontAwesome(SAVE_ICON, WidgetFontAwesome.IconSize.MEDIUM);
        save.setColor(COLOR_DISABLED);
        save.setPosition(columnWidth - 16, entryHeight-4);
        save.addListener(MouseEnterEvent.class, (event, widget) -> {
            save.setColor(COLOR_ENABLED);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        save.addListener(MouseExitEvent.class, (event, widget) -> {
            save.setColor(COLOR_DISABLED);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        save.addListener(MouseClickEvent.class, (event, widget) -> {
            String inputValue = inputField.getValue().toUpperCase();
            try {
                Enum enumVal = Enum.valueOf(enumClazz, inputValue);
                if (enumVal == null) {
                    return WidgetEventResult.CONTINUE_PROCESSING;
                }

                value.set(enumVal);
                value.save();
                hideErrorIcon();
            } catch (IllegalArgumentException e) {
                showErrorIcon(I18n.format("libnonymous.config.error.invalid_enum"));
            }
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        this.add(save);
    }
}
