package com.davenonymous.libnonymous.serialization.packetbuffer;

import com.davenonymous.libnonymous.utils.BlockStateSerializationHelper;
import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PacketBufferFieldHandlers {
    private static final Map<Class<?>, Pair<Reader, Writer>> packetBufferHandlers = new HashMap<>();

    static {
        addIOHandler(boolean[].class, buf -> {
            int size = buf.readInt();
            boolean[] result = new boolean[size];
            for(int i = 0; i < size; i++) {
                result[i] = buf.readBoolean();
            }
            return result;
        }, (booleans, buf) -> {
            buf.writeInt(booleans.length);
            for(boolean b : booleans) {
                buf.writeBoolean(b);
            }
        });
        addIOHandler(boolean.class, buf -> buf.readBoolean(), (b, buf) -> buf.writeBoolean(b));
        addIOHandler(Boolean.class, buf -> buf.readBoolean(), (b, buf) -> buf.writeBoolean(b));

        addIOHandler(int.class, buf -> buf.readInt(), (i, buf) -> buf.writeInt(i));
        addIOHandler(Integer.class, buf -> buf.readInt(), (i, buf) -> buf.writeInt(i));

        addIOHandler(float.class, buf -> buf.readFloat(), (f, buf) -> buf.writeFloat(f));
        addIOHandler(Float.class, buf -> buf.readFloat(), (f, buf) -> buf.writeFloat(f));

        addIOHandler(double.class, buf -> buf.readDouble(), (d, buf) -> buf.writeDouble(d));
        addIOHandler(Double.class, buf -> buf.readDouble(), (d, buf) -> buf.writeDouble(d));

        addIOHandler(long.class, buf -> buf.readLong(), (l, buf) -> buf.writeLong(l));
        addIOHandler(Long.class, buf -> buf.readLong(), (l, buf) -> buf.writeLong(l));

        addIOHandler(String.class, buf -> buf.readString(), (s, buf) -> buf.writeString(s));

        addIOHandler(ItemStack.class, buf -> buf.readItemStack(), (itemStack, buf) -> buf.writeItemStack(itemStack));

        addIOHandler(Ingredient.class, buf -> Ingredient.read(buf), (ingredient, buf) -> ingredient.write(buf));

        addIOHandler(Enum.class, buf -> {
            try {
                Class clz = Class.forName(buf.readString());
                return buf.readEnumValue(clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }, (anEnum, buf) -> {
            buf.writeString(anEnum.getClass().getName());
            buf.writeEnumValue(anEnum);
        });

        addIOHandler(Class.class, buf -> {
            try {
                return Class.forName(buf.readString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }, (aClass, buf) -> buf.writeString(aClass.getName()));

        addIOHandler(ResourceLocation.class, buf -> buf.readResourceLocation(), (resourceLocation, buf) -> buf.writeResourceLocation(resourceLocation));

        addIOHandler(BlockPos.class, buf -> buf.readBlockPos(), (pos, buf) -> buf.writeBlockPos(pos));

        addIOHandler(BlockState.class,
            buf -> BlockStateSerializationHelper.deserializeBlockState(buf),
            (blockState, buf) -> BlockStateSerializationHelper.serializeBlockState(buf, blockState));

        addIOHandler(UUID.class, buf -> buf.readUniqueId(), (uuid, buf) -> buf.writeUniqueId(uuid));

        addIOHandler(INBTSerializable.class, buf -> {
            CompoundNBT store = buf.readCompoundTag();
            INBT v = store.get("v");
            String className = store.getString("c");
            try {
                Class clz = Class.forName(className);
                INBTSerializable obj = (INBTSerializable)clz.getConstructor().newInstance();
                obj.deserializeNBT(v);
                return obj;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            return null;
        }, (inbtSerializable, buf) -> {
            CompoundNBT store = new CompoundNBT();
            store.putString("c", inbtSerializable.getClass().getName());
            store.put("v", inbtSerializable.serializeNBT());
            buf.writeCompoundTag(store);
        });

        addIOHandler(Map.class, buf -> {
            if(buf.readBoolean()) {
                // is Empty
                return Collections.emptyMap();
            }

            Map result = new HashMap();
            try {
                Class keyClass = Class.forName(buf.readString());
                if (!hasIOHandler(keyClass)) {
                    Logz.warn("No PacketBuffer deserialization methods for keys in map (type='{}') exists.", keyClass);
                    return Collections.emptyMap();
                }

                Class valueClass = Class.forName(buf.readString());
                if (!hasIOHandler(valueClass)) {
                    Logz.warn("No PacketBuffer deserialization methods for values in map (type='{}') exists.", valueClass);
                    return Collections.emptyMap();
                }

                int entryCount = buf.readInt();
                Reader keyReader = getIOHandler(keyClass).getLeft();
                Reader valueReader = getIOHandler(valueClass).getLeft();

                for(int entryNum = 0; entryNum < entryCount; entryNum++) {
                    Object keyObject = keyReader.read(buf);
                    Object valueObject = valueReader.read(buf);

                    result.put(keyObject, valueObject);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (map, buf) -> {
            buf.writeBoolean(map.isEmpty());

            if(!map.isEmpty()) {
                Class keyClass = map.keySet().toArray()[0].getClass();
                if(!hasIOHandler(keyClass)) {
                    Logz.warn("No PacketBuffer deserialization methods for keys in map (type='{}') exists.", keyClass);
                    return;
                }

                Class valueClass = map.values().toArray()[0].getClass();
                if(!hasIOHandler(valueClass)) {
                    Logz.warn("No PacketBuffer deserialization methods for values in map (type='{}') exists.", valueClass);
                    return;
                }

                buf.writeString(keyClass.getName());
                buf.writeString(valueClass.getName());
                buf.writeInt(map.entrySet().size());

                Writer keyWriter = getIOHandler(keyClass).getRight();
                Writer valueWriter = getIOHandler(valueClass).getRight();

                for(Object e : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) e;
                    keyWriter.write(entry.getKey(), buf);
                    valueWriter.write(entry.getValue(), buf);
                }
            }
        });


        addIOHandler(List.class, buf -> {
            if(buf.readBoolean()) {
                return Collections.emptyList();
            }

            ArrayList result = new ArrayList();
            try {
                Class valueClass = Class.forName(buf.readString());
                if (!hasIOHandler(valueClass)) {
                    Logz.warn("No PacketBuffer deserialization methods for values in list (type='{}') exists.", valueClass);
                    return Collections.emptyList();
                }

                int entryCount = buf.readInt();
                Reader valueReader = getIOHandler(valueClass).getLeft();

                for (int entryNum = 0; entryNum < entryCount; entryNum++) {
                    Object valueObject = valueReader.read(buf);
                    result.add(valueObject);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (list, buf) -> {
            buf.writeBoolean(list.isEmpty());

            if(!list.isEmpty()) {
                Class valueClass = list.toArray()[0].getClass();
                if(!hasIOHandler(valueClass)) {
                    Logz.warn("No PacketBuffer serialization methods for values in list (type='{}') exists.", valueClass);
                    return;
                }

                buf.writeString(valueClass.getName());
                buf.writeInt(list.size());

                Writer valueWriter = getIOHandler(valueClass).getRight();
                for(Object e : list) {
                    valueWriter.write(e, buf);
                }
            }
        });

        addIOHandler(Queue.class, buf -> {
            if(buf.readBoolean()) {
                return new ArrayDeque();
            }

            ArrayDeque result = new ArrayDeque<>();
            try {
                Class valueClass = Class.forName(buf.readString());
                if (!hasIOHandler(valueClass)) {
                    Logz.warn("No PacketBuffer deserialization methods for values in list (type='{}') exists.", valueClass);
                    return new ArrayDeque<>();
                }

                int entryCount = buf.readInt();
                Reader valueReader = getIOHandler(valueClass).getLeft();

                for (int entryNum = 0; entryNum < entryCount; entryNum++) {
                    Object valueObject = valueReader.read(buf);
                    result.add(valueObject);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (queue, buf) -> {
            buf.writeBoolean(queue.isEmpty());

            if(!queue.isEmpty()) {
                Class valueClass = queue.toArray()[0].getClass();
                if(!hasIOHandler(valueClass)) {
                    Logz.warn("No PacketBuffer serialization methods for values in list (type='{}') exists.", valueClass);
                    return;
                }

                buf.writeString(valueClass.getName());
                buf.writeInt(queue.size());

                Writer valueWriter = getIOHandler(valueClass).getRight();
                for(Object e : queue) {
                    valueWriter.write(e, buf);
                }
            }
        });

        addIOHandler(byte.class, buf -> buf.readByte(), (b, buf) -> buf.writeByte(b));
    }

    public static <T extends Object> void addIOHandler (Class<T> type, Reader<T> reader, Writer<T> writer) {
        packetBufferHandlers.put(type, Pair.of(reader, writer));
    }

    public static boolean hasIOHandler(Class clz) {
        if(packetBufferHandlers.containsKey(clz)) {
            return true;
        }

        for(Class iface : clz.getInterfaces()) {
            if(packetBufferHandlers.containsKey(iface)) {
                return true;
            }
        }

        Class superClass = clz.getSuperclass();
        if(superClass == null) {
            return false;
        }

        return hasIOHandler(superClass);
    }

    public static Pair<Reader, Writer> getIOHandler(Class clz) {
        if(packetBufferHandlers.containsKey(clz)) {
            return packetBufferHandlers.get(clz);
        }

        for(Class iface : clz.getInterfaces()) {
            if(packetBufferHandlers.containsKey(iface)) {
                return packetBufferHandlers.get(iface);
            }
        }

        Class superClass = clz.getSuperclass();
        if(superClass == null) {
            return null;
        }

        return getIOHandler(superClass);
    }


    // Functional interfaces
    public interface Writer<T extends Object> {
        void write(T t, PacketBuffer buf);
    }

    public interface Reader<T extends Object> {
        T read(PacketBuffer buf);
    }
}
