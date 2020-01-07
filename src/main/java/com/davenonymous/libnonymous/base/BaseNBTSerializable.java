package com.davenonymous.libnonymous.base;


import com.davenonymous.libnonymous.serialization.FieldUtils;
import com.davenonymous.libnonymous.serialization.NBTFieldSerializationData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public class BaseNBTSerializable implements INBTSerializable<CompoundNBT> {
    private List<NBTFieldSerializationData> NBTActions;
    private boolean isDirty = false;

    public BaseNBTSerializable() {
        this.NBTActions = FieldUtils.initSerializableStoreFields(this.getClass());
    }

    @Override
    public CompoundNBT serializeNBT() {
        return FieldUtils.writeFieldsToNBT(NBTActions, this, new CompoundNBT(), data -> true);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        FieldUtils.readFieldsFromNBT(NBTActions, this, nbt, data -> true);
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
