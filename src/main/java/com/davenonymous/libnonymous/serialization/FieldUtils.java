package com.davenonymous.libnonymous.serialization;

import com.davenonymous.libnonymous.utils.Logz;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class FieldUtils {
    private static Map<Class, List<Field>> lookupCache = new HashMap<>();
    private static Map<Class, List<NBTFieldSerializationData>> classNbtCache = new HashMap<>();
    private static Map<Class, List<ByteBufFieldSerializationData>> classByteBufCache = new HashMap<>();

    public static List<Field> getAllDeclaredFields(Class clz) {
        if(!lookupCache.containsKey(clz)) {
            List<Field> fields = FieldUtils.getAllDeclaredFields(new ArrayList<>(), clz);
            fields.sort(Comparator.comparing(o -> o.getClass().getName()));
            lookupCache.put(clz, fields);
        }

        return lookupCache.get(clz);
    }

    private static List<Field> getAllDeclaredFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllDeclaredFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static List<NBTFieldSerializationData> initSerializableStoreFields(Class clz) {
        if(classNbtCache.containsKey(clz)) {
            return classNbtCache.get(clz);
        }

        List<NBTFieldSerializationData> actionList = new ArrayList<>();

        for(Field field : FieldUtils.getAllDeclaredFields(clz)) {
            Annotation annotation = field.getDeclaredAnnotation(Store.class);
            if(annotation != null && annotation instanceof Store) {
                if(!FieldHandlers.hasNBTHandler(field.getType())) {
                    Logz.warn("No NBT serialization methods for field='%s' (type='%s') in class='%s' exists.", field.getName(), field.getType().getSimpleName(), clz.getSimpleName());
                    continue;
                }

                Store storeAnnotation = (Store)annotation;
                String key = storeAnnotation.key();
                if(key.equals("")) {
                    key = field.getName();
                }
                actionList.add(new NBTFieldSerializationData(field, key, storeAnnotation.storeWithItem(), storeAnnotation.sendInUpdatePackage()));
                field.setAccessible(true);
            }
        }

        classNbtCache.put(clz, actionList);
        return actionList;
    }

    public static List<ByteBufFieldSerializationData> initSerializableSyncFields(Class clz) {
        if(classByteBufCache.containsKey(clz)) {
            return classByteBufCache.get(clz);
        }

        List<ByteBufFieldSerializationData> actionList = new ArrayList<>();

        for(Field field : FieldUtils.getAllDeclaredFields(clz)) {
            Annotation annotation = field.getDeclaredAnnotation(Sync.class);
            if(annotation != null && annotation instanceof Sync) {
                if(!FieldHandlers.hasIOHandler(field.getType())) {
                    Logz.warn("No ByteBuf serialization methods for field='%s' (type='%s') in class='%s' exists.", field.getName(), field.getType().getSimpleName(), clz.getSimpleName());
                    continue;
                }

                Sync syncAnnotation = (Sync)annotation;
                actionList.add(new ByteBufFieldSerializationData(field));
                field.setAccessible(true);
            }
        }

        classByteBufCache.put(clz, actionList);
        return actionList;
    }

    public static CompoundNBT writeFieldsToNBT(List<NBTFieldSerializationData> NBTActions, Object source, CompoundNBT targetCompound, Predicate<NBTFieldSerializationData> test) {
        for(NBTFieldSerializationData data : NBTActions) {
            if(!test.test(data)) {
                continue;
            }

            try {
                Object value = data.field.get(source);
                data.writer.write(data.key, value, targetCompound);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return targetCompound;
    }

    public static void readFieldsFromNBT(List<NBTFieldSerializationData> NBTActions, Object target, CompoundNBT sourceCompound, Predicate<NBTFieldSerializationData> test) {
        for(NBTFieldSerializationData data : NBTActions) {
            if(!test.test(data)) {
                continue;
            }

            try {
                Object value = data.reader.read(data.key, sourceCompound);
                data.field.set(target, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFieldsToByteBuf(List<ByteBufFieldSerializationData> ioActions, Object source, ByteBuf targetBuffer, Predicate<ByteBufFieldSerializationData> test) {
        for(ByteBufFieldSerializationData data : ioActions) {
            if(!test.test(data)) {
                continue;
            }

            try {
                Object value = data.field.get(source);
                data.writer.write(value, targetBuffer);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readFieldsFromByteBuf(List<ByteBufFieldSerializationData> ioActions, Object target, ByteBuf sourceBuffer, Predicate<ByteBufFieldSerializationData> test) {
        for(ByteBufFieldSerializationData data : ioActions) {
            if(!test.test(data)) {
                continue;
            }

            try {
                Object value = data.reader.read(sourceBuffer);
                data.field.set(target, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
