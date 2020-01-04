package com.davenonymous.libnonymous.gui.framework.event;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;

public class TabChangedEvent extends ValueChangedEvent<WidgetPanel> {
    public TabChangedEvent(WidgetPanel oldValue, WidgetPanel newValue) {
        super(oldValue, newValue);
    }
}
