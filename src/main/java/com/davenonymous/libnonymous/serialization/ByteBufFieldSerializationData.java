package com.davenonymous.libnonymous.serialization;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;

public class ByteBufFieldSerializationData {
    public FieldHandlers.Reader reader;
    public FieldHandlers.Writer writer;
    public Field field;

    public ByteBufFieldSerializationData(Field field) {
        this.field = field;

        Pair<FieldHandlers.Reader, FieldHandlers.Writer> pair = FieldHandlers.getIOHandler(field.getType());
        this.reader = pair.getLeft();
        this.writer = pair.getRight();
    }
}
