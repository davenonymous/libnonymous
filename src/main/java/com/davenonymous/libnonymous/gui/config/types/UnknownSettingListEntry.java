package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.utils.FontAwesomeHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class UnknownSettingListEntry extends SettingListEntry {
    public UnknownSettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super(optionKey, comment, value, defaultValue, columnWidth);
    }

    @Override
    public void fillPanel(int entryHeight) {
        this.setSize(columnWidth, entryHeight+17);

        WidgetTextBox textBox = new WidgetTextBox("Sorry, this config type is not supported.", COLOR_ERRORED.getRGB());
        textBox.setSize(columnWidth-10, 9);
        textBox.setPosition(28, 30);
        this.add(textBox);

        WidgetFontAwesome sadIcon = new WidgetFontAwesome(FontAwesomeHelper.getRandomAnimalIcon(), WidgetFontAwesome.IconSize.MEDIUM);
        sadIcon.setColor(COLOR_ERRORED);
        sadIcon.setPosition(6, 24);
        this.add(sadIcon);
    }
}
