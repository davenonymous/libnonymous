package com.davenonymous.libnonymous.gui.config;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetListEntry;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import net.minecraft.util.text.StringTextComponent;

public class CategoryListEntry extends WidgetListEntry {
    public CategoryListEntry(String category, int columnWidth) {
        super();

        this.setSize(columnWidth, 12);

        WidgetTextBox label = new WidgetTextBox(category, 0xA0FFFFFF);
        label.setDimensions(0, 1, columnWidth, 9);
        label.setTooltipLines(new StringTextComponent(category));
        this.add(label);
    }
}
