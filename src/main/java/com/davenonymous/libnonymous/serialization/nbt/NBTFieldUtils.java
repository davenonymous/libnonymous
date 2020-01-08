package com.davenonymous.libnonymous.serialization.nbt;

import com.davenonymous.libnonymous.serialization.FieldUtils;
import com.davenonymous.libnonymous.serialization.Store;
import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.nbt.CompoundNBT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class NBTFieldUtils {
    private static Map<Class, List<NBTFieldSerializationData>> classNbtCache = new HashMap<>();

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

    public static List<NBTFieldSerializationData> initSerializableStoreFields(Class clz) {
        if(classNbtCache.containsKey(clz)) {
            return classNbtCache.get(clz);
        }

        List<NBTFieldSerializationData> actionList = new ArrayList<>();

        for(Field field : FieldUtils.getAllDeclaredFields(clz)) {
            Annotation annotation = field.getDeclaredAnnotation(Store.class);
            if(annotation != null && annotation instanceof Store) {
                if(!NBTFieldHandlers.hasNBTHandler(field.getType())) {
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
}
