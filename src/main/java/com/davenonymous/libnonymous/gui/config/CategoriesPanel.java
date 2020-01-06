package com.davenonymous.libnonymous.gui.config;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetList;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import net.minecraft.client.resources.I18n;

public class CategoriesPanel extends WidgetPanel {
    public WidgetList categoryList;

    public CategoriesPanel(int columnWidths, int desiredHeight) {
        super();
        this.setPosition(5, desiredHeight / 2);
        this.setSize(columnWidths, desiredHeight/2);
        this.setVisible(false);

        WidgetTextBox labelCategories = new WidgetTextBox(I18n.format("libnonymous.config.gui.label.category"), 0xC0000000);
        labelCategories.setDimensions(0, 6, columnWidths, 9);
        this.add(labelCategories);

        categoryList = new WidgetList();
        categoryList.setDimensions(0, 16, columnWidths, (desiredHeight/2)-21);
        this.add(categoryList);
    }
}
