package com.storemgmt.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class JsonLoader {

    private static JsonObject json;

    public static void load(String path) {
        try (FileReader reader = new FileReader(path)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            json = new JsonObject();
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

    public static double getDouble(String path) {
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

        return current.get(lastKey).getAsDouble();
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

    public static void remove(String path) {
        String[] parts = path.split("\\.");
        JsonObject parent = json;

        for (int i = 0; i < parts.length - 1; i++) {
            parent = parent.getAsJsonObject(parts[i]);
        }

        parent.remove(parts[parts.length - 1]);
    }

    public static void add(String path, JsonElement value) {
        String[] keys = path.split("\\.");
        JsonObject current = json;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if (!current.has(key) || !current.get(key).isJsonObject()) {
                JsonObject newObj = new JsonObject();
                current.add(key, newObj);
                current = newObj;
            } else {
                current = current.getAsJsonObject(key);
            }
        }

        String lastKey = keys[keys.length - 1];
        current.add(lastKey, value);
    }

    public static void set(String oldPath, int newValue) {
        remove(oldPath);
        add(oldPath, new JsonPrimitive(newValue));
    }

    public static void addString(String path, String value) {
        add(path, new JsonPrimitive(value));
    }

    public static void addInt(String path, int value) {
        add(path, new JsonPrimitive(value));
    }

    public static void save() {
        try (FileWriter writer = new FileWriter("config.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}