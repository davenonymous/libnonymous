package com.davenonymous.libnonymous.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JsonHelpers {

	public static JsonObject insertAsArray(JsonObject parent, String key, JsonElement toInsert) {
		if(parent.has(key)) {
			parent.getAsJsonArray(key).add(toInsert);
		} else {
			var newArray = new JsonArray();
			newArray.add(toInsert);
			parent.add(key, newArray);
		}

		return parent;
	}

	public static JsonObject insertAsArrayOrSingle(JsonObject parent, String key, JsonObject toInsert) {
		if(parent.has(key)) {
			var originalElement = parent.get(key);
			if(originalElement.isJsonArray()) {
				originalElement.getAsJsonArray().add(toInsert);
			} else {
				// Turn the single value into an array
				parent.remove(key);
				var arrayReplacement = new JsonArray(2);
				arrayReplacement.add(originalElement);
				arrayReplacement.add(toInsert);

				parent.add(key, arrayReplacement);
			}
		} else {
			parent.add(key, toInsert);
		}

		return parent;
	}

	public static Ingredient getIngredientFromArrayOrSingle(JsonElement json) {
		List<Ingredient> ingredients = new ArrayList<>();
		var allTags = ItemTags.getAllTags();
		processArrayOrSingle(json, jsonElement -> {
			if(!jsonElement.isJsonObject()) {
				return;
			}

			var obj = jsonElement.getAsJsonObject();
			if(obj.has("item")) {
				ingredients.add(Ingredient.fromJson(obj));
			} else if(obj.has("tag")) {
				var tagName = obj.get("tag").getAsString();
				var tagRL = ResourceLocation.tryParse(tagName);
				if(tagRL != null && allTags.hasTag(tagRL)) {
					var tag = allTags.getTag(tagRL);
					ingredients.add(Ingredient.of(tag));
				}
			}
		});

		return Ingredient.merge(ingredients);
	}


	public static int processArrayOrSingle(JsonElement json, Consumer<JsonElement> c) {
		if(json == null) {
			return 0;
		}

		if(json.isJsonArray()) {
			var array = json.getAsJsonArray();
			int count = array.size();
			array.forEach(c);

			return count;
		}

		c.accept(json);
		return 1;
	}
}