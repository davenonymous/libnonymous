package com.davenonymous.libnonymous.datagen;

import com.davenonymous.libnonymous.Libnonymous;
import com.davenonymous.libnonymous.base.BaseLanguageProvider;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetRedstoneMode;
import net.minecraft.data.DataGenerator;

public class DatagenTranslations extends BaseLanguageProvider {
	public DatagenTranslations(DataGenerator gen, String locale) {
		super(gen, Libnonymous.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		add(WidgetRedstoneMode.REDSTONE_IGNORE, "Ignore nearby redstone signals");
		add(WidgetRedstoneMode.REDSTONE_REQUIRED, "Only run with a nearby redstone signal");
		add(WidgetRedstoneMode.REDSTONE_REJECTED, "Prevent running with a nearby redstone signal");
	}
}