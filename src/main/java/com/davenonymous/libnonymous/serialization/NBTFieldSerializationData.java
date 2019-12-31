package com.davenonymous.libnonymous.serialization;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;

public class NBTFieldSerializationData {
    public FieldHandlers.NbtReader reader;
    public FieldHandlers.NbtWriter writer;
    public Field field;
    public String key;
    public boolean storeWithItem;
    public boolean sendInUpdatePackage;

    public NBTFieldSerializationData(Field field, String key, boolean storeWithItem, boolean sendInUpdatePackage) {
        this.field = field;
        this.key = key;
        this.storeWithItem = storeWithItem;
        this.sendInUpdatePackage = sendInUpdatePackage;

        Pair<FieldHandlers.NbtReader, FieldHandlers.NbtWriter> pair = FieldHandlers.getNBTHandler(field.getType());
        this.reader = pair.getLeft();
        this.writer = pair.getRight();
    }
}
