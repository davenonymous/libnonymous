package com.davenonymous.libnonymous.gui.config;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetListEntry;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;

public class SpecListEntry extends WidgetListEntry {
    public SpecListEntry(String filename, int columnWidth) {
        super();

        this.setSize(columnWidth, 12);

        WidgetTextBox label = new WidgetTextBox(filename, 0xA0FFFFFF);
        label.setDimensions(0, 1, columnWidth, 9);
        this.add(label);
    }
}
