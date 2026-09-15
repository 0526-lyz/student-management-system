package com.student.web;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * JSON 工具类：基于 Gson，统一处理时间格式，并排除密码字段，避免泄露敏感信息。
 */
public final class Json {

    private static final Gson GSON = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return "password".equals(f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> incomingClass) {
                    return false;
                }
            })
            .registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, type, ctx) -> {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return new JsonPrimitive(fmt.format(src));
            })
            .create();

    private Json() {
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    public static JsonObject parse(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static String str(JsonObject obj, String key) {
        JsonElement e = obj.get(key);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    public static String str(JsonObject obj, String key, String defaultValue) {
        String v = str(obj, key);
        return v == null ? defaultValue : v;
    }

    public static int integer(JsonObject obj, String key, int defaultValue) {
        JsonElement e = obj.get(key);
        return e == null || e.isJsonNull() ? defaultValue : e.getAsInt();
    }

    public static double decimal(JsonObject obj, String key, double defaultValue) {
        JsonElement e = obj.get(key);
        return e == null || e.isJsonNull() ? defaultValue : e.getAsDouble();
    }
}
