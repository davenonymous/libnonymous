package com.davenonymous.libnonymous.base;

import com.davenonymous.libnonymous.serialization.nbt.NBTFieldSerializationData;
import com.davenonymous.libnonymous.serialization.nbt.NBTFieldUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public class BaseNBTSerializable implements INBTSerializable<CompoundTag> {
	private List<NBTFieldSerializationData> NBTActions;
	private boolean isDirty = false;

	public BaseNBTSerializable() {
		this.NBTActions = NBTFieldUtils.initSerializableStoreFields(this.getClass());
	}

	@Override
	public CompoundTag serializeNBT() {
		return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, new CompoundTag(), data -> true);
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
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