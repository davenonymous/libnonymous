package com.davenonymous.libnonymous.datagen;

import com.davenonymous.libnonymous.Libnonymous;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Libnonymous.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	private static void generateServerData(DataGenerator generator, ExistingFileHelper existingFileHelper) {
	}

	private static void generateClientData(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		generator.addProvider(true, new DatagenTranslations(generator, "en_us"));
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		var generator = event.getGenerator();
		if(event.includeServer()) {
			generateServerData(generator, event.getExistingFileHelper());
		}

		if(event.includeClient()) {
			generateClientData(generator, event.getExistingFileHelper());
		}
	}
}