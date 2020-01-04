package com.davenonymous.libnonymous.gui.framework.widgets;

import net.minecraft.util.ResourceLocation;

public interface IValueProvider<T> {
    ResourceLocation getId();
    void setId(ResourceLocation location);
    T getValue();
    void setValue(T value);
}
