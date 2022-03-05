package com.davenonymous.libnonymous.base;

import com.davenonymous.libnonymous.serialization.nbt.NBTFieldSerializationData;
import com.davenonymous.libnonymous.serialization.nbt.NBTFieldUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;

public class BaseWorldSavedData extends SavedData {
	private final List<NBTFieldSerializationData> NBTActions;

	protected BaseWorldSavedData() {
		this.NBTActions = NBTFieldUtils.initSerializableStoreFields(this.getClass());
	}

	public BaseWorldSavedData(CompoundTag tag) {
		this();
		NBTFieldUtils.readFieldsFromNBT(this.NBTActions, this, tag, data -> true);
	}

	@Override
	public CompoundTag save(CompoundTag pCompoundTag) {
		return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, pCompoundTag, data -> true);
	}
}