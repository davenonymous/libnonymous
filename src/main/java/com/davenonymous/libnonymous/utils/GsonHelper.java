package com.davenonymous.libnonymous.utils;

import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.serialization.MultiBlockModelSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(MultiblockBlockModel.class, new MultiBlockModelSerializer())
            .create();
}
