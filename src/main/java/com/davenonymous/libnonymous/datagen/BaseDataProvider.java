package com.davenonymous.libnonymous.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseDataProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


	private final DataGenerator generator;
	private final Type type;

	private Map<String, JsonObject> values = new HashMap<>();

	public abstract void addValues();

	public abstract String getModId();

	public void add(String path, JsonObject value) {
		this.values.put(path, value);
	}

	public enum Type {
		ASSETS, DATA
	}

	public static float getRounded(double d) {
		var df = new DecimalFormat("#.##");
		var symbols = df.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);

		return Float.parseFloat(df.format(d));
	}

	public BaseDataProvider(DataGenerator generator, Type type) {
		this.type = type;
		this.generator = generator;
	}


	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		addValues();

		return CompletableFuture.allOf(values.entrySet().stream().map((table) -> {
			String s = table.getKey();
			JsonObject jsonObject = table.getValue();

			return saveValue(cache, s, jsonObject);
		}).toArray(CompletableFuture[]::new));
	}

	private CompletableFuture<?> saveValue(CachedOutput cache, String key, JsonObject jsonObject) {
		Path mainOutput = generator.getPackOutput().getOutputFolder();
		String pathSuffix = (type == Type.ASSETS ? "assets" : "data") + "/" + getModId() + "/" + key + ".json";

		Path outputPath = mainOutput.resolve(pathSuffix);
		return DataProvider.saveStable(cache, jsonObject, outputPath);
	}
}