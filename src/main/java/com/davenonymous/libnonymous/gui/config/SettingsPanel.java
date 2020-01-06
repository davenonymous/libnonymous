package com.davenonymous.libnonymous.gui.config;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetList;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import net.minecraft.client.resources.I18n;

public class SettingsPanel extends WidgetPanel {
    public WidgetList settingsList;

    public SettingsPanel(int columnWidths, int desiredHeight) {
        super();
        this.setPosition(columnWidths+10, 0);
        this.setSize(columnWidths*2+5, desiredHeight);
        this.setVisible(false);

        WidgetTextBox labelCategories = new WidgetTextBox(I18n.format("libnonymous.config.gui.label.settings"), 0xC0000000);
        labelCategories.setDimensions(0, 6, columnWidths, 9);
        this.add(labelCategories);

        settingsList = new WidgetList();
        settingsList.setDimensions(0, 16, columnWidths*2+5, desiredHeight-21);
        this.add(settingsList);
    }
}