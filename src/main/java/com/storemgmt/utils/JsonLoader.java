package com.storemgmt.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.Set;

public class JsonLoader {

    private static JsonObject json;

    public static void load(String path) {
        try (FileReader reader = new FileReader(path)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            json = new JsonObject(); // fallback empty JSON
        }
    }

    public static JsonObject get() {
        return json;
    }

    public static int getInt(String path) {
        String[] keys = path.split("\\.");
        JsonObject current = json;

        for (int i = 0; i < keys.length - 1; i++) {
            if (!current.has(keys[i]) || !current.get(keys[i]).isJsonObject()) {
                throw new IllegalArgumentException("Missing key: " + keys[i]);
            }
            current = current.getAsJsonObject(keys[i]);
        }

        String lastKey = keys[keys.length - 1];
        if (!current.has(lastKey)) {
            throw new IllegalArgumentException("Missing key: " + lastKey);
        }

        return current.get(lastKey).getAsInt();
    }

    public static String getString(String path) {
        String[] keys = path.split("\\.");
        JsonObject current = json;

        for (int i = 0; i < keys.length - 1; i++) {
            if (!current.has(keys[i]) || !current.get(keys[i]).isJsonObject()) {
                throw new IllegalArgumentException("Missing key: " + keys[i]);
            }
            current = current.getAsJsonObject(keys[i]);
        }

        String lastKey = keys[keys.length - 1];
        if (!current.has(lastKey)) {
            throw new IllegalArgumentException("Missing key: " + lastKey);
        }

        return current.get(lastKey).getAsString();
    }

    public static Set<String> getKeys(String path) {
        String[] keys = path.split("\\.");
        JsonObject current = json;

        for (int i = 0; i < keys.length; i++) {
            if (!current.has(keys[i]) || !current.get(keys[i]).isJsonObject()) {
                throw new IllegalArgumentException("Missing key: " + keys[i]);
            }
            current = current.getAsJsonObject(keys[i]);
        }

        return current.keySet();
    }

    public static String getKey(String path) {
        String[] keys = path.split("\\.");
        return keys[keys.length - 1];
    }
}
