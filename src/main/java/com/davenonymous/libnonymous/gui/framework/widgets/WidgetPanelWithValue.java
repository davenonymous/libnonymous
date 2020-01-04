package com.davenonymous.libnonymous.gui.framework.widgets;

import net.minecraft.util.ResourceLocation;

public abstract class WidgetPanelWithValue<T> extends WidgetPanel implements IValueProvider<T> {
    private ResourceLocation id;

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void setId(ResourceLocation location) {
        this.id = location;
    }
}
