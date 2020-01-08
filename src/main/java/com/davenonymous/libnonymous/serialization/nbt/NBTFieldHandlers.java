package com.davenonymous.libnonymous.serialization.nbt;

import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NBTFieldHandlers {
    private static final Map<Class<?>, Pair<NbtReader, NbtWriter>> nbtHandlers = new HashMap<>();

    static {
        addNBTHandler(boolean[].class, (key, tag) -> {
            ListNBT listNBT = tag.getList(key, Constants.NBT.TAG_SHORT);
            boolean[] result = new boolean[listNBT.size()];
            for(int i = 0; i < result.length; i++) {
                result[i] = listNBT.getShort(i) == 1;
            }

            return null;
        }, (key, booleans, tag) -> {
            ListNBT listNBT = new ListNBT();
            for(boolean b : booleans) {
                listNBT.add(new ShortNBT(b ? (short)1 : (short)0));
            }

            tag.put(key, listNBT);
        });
        addNBTHandler(boolean.class, (key, tag) -> tag.getBoolean(key), (key, aBoolean, tag) -> tag.putBoolean(key, aBoolean));
        addNBTHandler(Boolean.class, (key, tag) -> tag.getBoolean(key), (key, aBoolean, tag) -> tag.putBoolean(key, aBoolean));

        addNBTHandler(int.class, (key, tag) -> tag.getInt(key), (key, val, tag) -> tag.putInt(key, val));
        addNBTHandler(Integer.class, (key, tag) -> tag.getInt(key), (key, integer, tag) -> tag.putInt(key, integer));

        addNBTHandler(float.class, (key, tag) -> tag.getFloat(key), (key, val, tag) -> tag.putFloat(key, val));
        addNBTHandler(Float.class, (key, tag) -> tag.getFloat(key), (key, val, tag) -> tag.putFloat(key, val));

        addNBTHandler(double.class, (key, tag) -> tag.getDouble(key), (key, val, tag) -> tag.putDouble(key, val));
        addNBTHandler(Double.class, (key, tag) -> tag.getDouble(key), (key, val, tag) -> tag.putDouble(key, val));

        addNBTHandler(long.class, (key, tag) -> tag.getLong(key), (key, val, tag) -> tag.putLong(key, val));
        addNBTHandler(Long.class, (key, tag) -> tag.getLong(key), (key, val, tag) -> tag.putLong(key, val));

        // This is actually covered by INBTSerializable, but our class/interface iteration method is too strict about this.
        addNBTHandler(ItemStack.class, (key, tag) -> ItemStack.read(tag.getCompound(key)), (key, itemStack, tag) -> tag.put(key, itemStack.serializeNBT()));

        addNBTHandler(Enum.class, ((key, tag) -> {
            CompoundNBT enumTag = tag.getCompound(key);
            try {
                Class clz = Class.forName(enumTag.getString("class"));
                return Enum.valueOf(clz, enumTag.getString("value"));
            } catch (ClassNotFoundException e) {
                Logz.warn("Could not find enum '%s' during NBT deserialization", tag.getString(key));
                e.printStackTrace();
            }
            return null;
        }), (key, anEnum, tag) -> {
            CompoundNBT result = new CompoundNBT();
            result.putString("class", anEnum.getClass().getName());
            result.putString("value", anEnum.name());

            tag.put(key, result);
        });

        addNBTHandler(Class.class, (key, tag) -> {
            if(key.equals("") || !tag.contains(key)) {
                return null;
            }

            try {
                return Class.forName(tag.getString(key));
            } catch (ClassNotFoundException e) {
                Logz.warn("Could not find class '%s' during NBT deserialization", tag.getString(key));
                e.printStackTrace();
            }
            return null;
        }, (key, aClass, tag) -> {
            if(aClass != null) {
                tag.putString(key, aClass.getName());
            }
        });

        addNBTHandler(ResourceLocation.class, (key, tag) -> {
            if(!tag.contains(key)) {
                return null;
            }

            return new ResourceLocation(tag.getString(key));
        }, (key, resourceLocation, tag) -> {
            if(resourceLocation == null) {
                return;
            }

            tag.putString(key, resourceLocation.toString());
        });

        addNBTHandler(BlockPos.class, (key, tag) -> {
            CompoundNBT container = tag.getCompound(key);
            return new BlockPos(container.getInt("x"), container.getInt("y"), container.getInt("z"));
        }, (key, pos, tag) -> {
            CompoundNBT container = new CompoundNBT();
            container.putInt("x", pos.getX());
            container.putInt("y", pos.getY());
            container.putInt("z", pos.getZ());
            tag.put(key, container);
        });

        addNBTHandler(String.class, (key, tag) -> tag.contains(key) ? tag.getString(key) : null, (key, s, tag) -> {
            if(s != null) {
                tag.putString(key, s);
            }
        });
        addNBTHandler(UUID.class, (key, tag) -> {
            if(!tag.contains(key)) {
                return null;
            }

            CompoundNBT containerTag = tag.getCompound(key);
            return containerTag.getUniqueId("");
        }, (key, uuid, tag) -> {
            if(uuid == null) {
                return;
            }

            CompoundNBT containerTag = new CompoundNBT();
            containerTag.putUniqueId("", uuid);
            tag.put(key, containerTag);
        });

        addNBTHandler(INBTSerializable.class, (key, tag) -> {
            CompoundNBT containerTag = tag.getCompound(key);
            String className = containerTag.getString("class");
            try {
                Class clz = Class.forName(className);
                INBTSerializable obj = (INBTSerializable)clz.getConstructor().newInstance();
                obj.deserializeNBT(containerTag.getCompound("data"));
                return obj;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }, (key, INBTSerializable, tag) -> {
            CompoundNBT containerTag = new CompoundNBT();
            containerTag.putString("class", INBTSerializable.getClass().getName());
            containerTag.put("data", INBTSerializable.serializeNBT());
            tag.put(key, containerTag);
        });

        addNBTHandler(Map.class, (key, tag) -> {
            CompoundNBT containerTag = tag.getCompound(key);
            if(!containerTag.contains("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.contains("entries")) {
                return new HashMap();
            }

            Map result = new HashMap();
            try {
                Class keyClass = Class.forName(containerTag.getString("keyClass"));
                if (!hasNBTHandler(keyClass)) {
                    Logz.warn("No NBT deserialization methods for keys in map (type='%s') exists.", keyClass);
                    return new HashMap();
                }

                Class valueClass = Class.forName(containerTag.getString("valueClass"));
                if (!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in map (type='%s') exists.", valueClass);
                    return new HashMap();
                }

                NbtReader keyReader = getNBTHandler(keyClass).getLeft();
                NbtReader valueReader = getNBTHandler(valueClass).getLeft();

                for(INBT baseTag : containerTag.getList("entries", Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT entry = (CompoundNBT) baseTag;
                    Object keyObject = keyReader.read("key", entry);
                    Object valueObject = valueReader.read("value", entry);

                    result.put(keyObject, valueObject);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (key, map, tag) -> {
            CompoundNBT containerTag = new CompoundNBT();
            containerTag.putBoolean("isEmpty", map.isEmpty());

            if(!map.isEmpty()) {
                Class keyClass = map.keySet().toArray()[0].getClass();
                if(!hasNBTHandler(keyClass)) {
                    Logz.warn("No NBT deserialization methods for keys in map (type='%s') exists.", keyClass);
                    return;
                }

                Class valueClass = map.values().toArray()[0].getClass();
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in map (type='%s') exists.", valueClass);
                    return;
                }

                containerTag.putString("keyClass", keyClass.getName());
                containerTag.putString("valueClass", valueClass.getName());

                NbtWriter keyWriter = getNBTHandler(keyClass).getRight();
                NbtWriter valueWriter = getNBTHandler(valueClass).getRight();

                ListNBT data = new ListNBT();
                for(Object e : map.entrySet()) {
                    CompoundNBT entryTag = new CompoundNBT();
                    Map.Entry entry = (Map.Entry) e;

                    keyWriter.write("key", entry.getKey(), entryTag);
                    valueWriter.write("value", entry.getValue(), entryTag);

                    data.add(entryTag);
                }

                containerTag.put("entries", data);
            }

            tag.put(key, containerTag);
        });

        addNBTHandler(List.class, (key, tag) -> {
            List result = new ArrayList();
            CompoundNBT containerTag = tag.getCompound(key);
            if(!containerTag.contains("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.contains("values")) {
                return result;
            }

            try {
                Class valueClass = Class.forName(containerTag.getString("valueClass"));
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in list (type='%s') exists.", valueClass);
                    return result;
                }

                NbtReader reader = getNBTHandler(valueClass).getLeft();
                for(INBT baseTag : containerTag.getList("values", Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT entry = (CompoundNBT)baseTag;
                    Object value = reader.read("data", entry);
                    result.add(value);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (key, list, tag) -> {
            CompoundNBT containerTag = new CompoundNBT();
            containerTag.putBoolean("isEmpty", list.isEmpty());

            if(!list.isEmpty()) {
                Class valueClass = list.get(0).getClass();
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT serialization methods for values in list (type='%s') exists.", valueClass.getName());
                    return;
                }

                containerTag.putString("valueClass", valueClass.getName());

                NbtWriter writer = getNBTHandler(valueClass).getRight();
                ListNBT data = new ListNBT();
                for(Object e : list) {
                    CompoundNBT entryContainerTag = new CompoundNBT();
                    writer.write("data", e, entryContainerTag);
                    data.add(entryContainerTag);
                }
                containerTag.put("values", data);
            }

            tag.put(key, containerTag);
        });

        addNBTHandler(Queue.class, (key, tag) -> {
            CompoundNBT containerTag = tag.getCompound(key);
            if(!containerTag.contains("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.contains("values")) {
                return new ArrayDeque<>();
            }

            Queue result = new ArrayDeque<>();
            try {

                Class valueClass = Class.forName(containerTag.getString("valueClass"));
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in queue (type='%s') exists.", valueClass);
                    return new ArrayDeque<>();
                }

                NbtReader reader = getNBTHandler(valueClass).getLeft();

                for(INBT baseTag : containerTag.getList("values", Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT entry = (CompoundNBT)baseTag;
                    Object value = reader.read("data", entry);
                    result.add(value);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (key, queue, tag) -> {
            CompoundNBT containerTag = new CompoundNBT();
            containerTag.putBoolean("isEmpty", queue.isEmpty());

            if(!queue.isEmpty()) {
                Class valueClass = queue.peek().getClass();
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT serialization methods for values in list (type='%s') exists.", valueClass.getName());
                    return;
                }

                containerTag.putString("valueClass", valueClass.getName());

                NbtWriter writer = getNBTHandler(valueClass).getRight();

                ListNBT data = new ListNBT();
                for(Object e : queue) {
                    CompoundNBT entryContainerTag = new CompoundNBT();
                    writer.write("data", e, entryContainerTag);
                    data.add(entryContainerTag);
                }
                containerTag.put("values", data);
            }

            tag.put(key, containerTag);
        });

    }


    public static <T extends Object> void addNBTHandler (Class<T> type, NbtReader<T> reader, NbtWriter<T> writer) {
        nbtHandlers.put(type, Pair.of(reader, writer));
    }

    public static boolean hasNBTHandler(Class clz) {
        if(nbtHandlers.containsKey(clz)) {
            return true;
        }

        for(Class iface : clz.getInterfaces()) {
            if(nbtHandlers.containsKey(iface)) {
                return true;
            }
        }

        Class superClass = clz.getSuperclass();
        if(superClass == null) {
            return false;
        }

        return hasNBTHandler(superClass);
    }

    public static Pair<NbtReader, NbtWriter> getNBTHandler(Class clz) {
        if(nbtHandlers.containsKey(clz)) {
            return nbtHandlers.get(clz);
        }

        for(Class iface : clz.getInterfaces()) {
            if(nbtHandlers.containsKey(iface)) {
                return nbtHandlers.get(iface);
            }
        }

        Class superClass = clz.getSuperclass();
        if(superClass == null) {
            return null;
        }

        return getNBTHandler(superClass);
    }

    public interface NbtWriter<T extends Object> {
        void write(String key, T t, CompoundNBT tag);
    }

    public interface NbtReader<T extends Object> {
        T read(String key, CompoundNBT tag);
    }

}
