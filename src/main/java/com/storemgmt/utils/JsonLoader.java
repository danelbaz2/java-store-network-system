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

    // Loads the JSON file from the given path
    public static void load(String path) {
        try (FileReader reader = new FileReader(path)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            json = new JsonObject(); // fallback empty JSON
        }
    }

    // Returns the loaded JSON object
    public static JsonObject get() {
        return json;
    }

    // Returns an integer value for a given path
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

    // Returns a string value for a given path
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

    // Returns a set of keys for a given path
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

    // Returns the last key in the path
    public static String getKey(String path) {
        String[] keys = path.split("\\.");
        return keys[keys.length - 1];
    }

    public static void remove(String path) {
        String[] keys = path.split("\\.");
        JsonObject current = json;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if (!current.has(key) || !current.get(key).isJsonObject()) {
                throw new IllegalArgumentException("Missing key: " + key);
            }
            current = current.getAsJsonObject(key);
        }

        String lastKey = keys[keys.length - 1];
        current.remove(lastKey);
    }

    // Adds a new key-value pair to the JSON object
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
        current.add(lastKey, value); // overwrites if exists
    }

    // Adds a string value to the JSON object
    public static void addString(String path, String value) {
        add(path, new JsonPrimitive(value));
    }

    // Adds an integer value to the JSON object
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