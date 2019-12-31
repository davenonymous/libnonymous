package com.davenonymous.libnonymous.serialization;

public @interface SerializationHandler {
    Class readClass() default void.class;
    Class writeClass() default void.class;
}
