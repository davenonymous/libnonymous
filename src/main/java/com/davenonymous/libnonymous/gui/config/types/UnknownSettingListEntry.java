package com.davenonymous.libnonymous.gui.config.types;

import net.minecraftforge.common.ForgeConfigSpec;

public class UnknownSettingListEntry extends SettingListEntry {
    public UnknownSettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super(optionKey, comment, value, defaultValue, columnWidth);
    }

    @Override
    public void fillPanel(int entryHeight) {
        this.addUnsupportedRow(entryHeight);
    }

    @Override
    public void setValueInInputField(Object defaultValue) {

    }
}
