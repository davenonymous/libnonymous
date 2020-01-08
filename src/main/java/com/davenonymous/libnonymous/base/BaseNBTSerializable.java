package com.davenonymous.libnonymous.base;


import com.davenonymous.libnonymous.serialization.nbt.NBTFieldSerializationData;
import com.davenonymous.libnonymous.serialization.nbt.NBTFieldUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public class BaseNBTSerializable implements INBTSerializable<CompoundNBT> {
    private List<NBTFieldSerializationData> NBTActions;
    private boolean isDirty = false;

    public BaseNBTSerializable() {
        this.NBTActions = NBTFieldUtils.initSerializableStoreFields(this.getClass());
    }

    @Override
    public CompoundNBT serializeNBT() {
        return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, new CompoundNBT(), data -> true);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, nbt, data -> true);
        afterLoad();
    }

    public void markDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void afterLoad() {

    }
}
