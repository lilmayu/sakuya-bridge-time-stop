package dev.mayuna.sakuyabridge.client.v2.frontend.lang;

import com.google.gson.*;
import dev.mayuna.sakuyabridge.client.v2.frontend.util.StringUtils;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Language pack for the client.
 */
@Data
public class LanguagePack {

    /**
     * GSON instance for serializing and deserializing language packs.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LanguagePack.class, new LanguagePackTypeAdapter())
            .create();

    private String id; // en_US, ja_JP, etc.
    private String name; // English, 日本語, etc.
    private String author; // Mayuna, etc.
    private String version; // 1.0, 1.1, etc.

    private Map<String, String> translations = new HashMap<>();

    private LanguagePack() {
    }

    /**
     * Load a language pack from a resource.
     *
     * @param json The JSON string of the language pack.
     *
     * @return The language pack.
     */
    public static LanguagePack loadFromJson(String json) {
        return GSON.fromJson(json, LanguagePack.class);
    }

    /**
     * Get a translation from the language pack.
     *
     * @param key The key of the translation.
     *
     * @return The translation.
     */
    public String getTranslation(String key) {
        return translations.get(key);
    }

    /**
     * Format a translation with arguments.
     *
     * @param key  The key of the translation.
     * @param args The arguments to format the translation with.
     *
     * @return The formatted translation.
     */
    public String formatTranslation(String key, Object... args) {
        String translation = getTranslation(key);

        if (translation == null) {
            return null;
        }

        return StringUtils.indexedFormat(translation, args);
    }

    public String toString() {
        return "LanguagePack{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Language pack type adapter for GSON.
     */
    public static class LanguagePackTypeAdapter implements JsonDeserializer<LanguagePack>, JsonSerializer<LanguagePack> {

        @Override
        public LanguagePack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            LanguagePack languagePack = new LanguagePack();
            languagePack.setId(jsonObject.get("id").getAsString());
            languagePack.setName(jsonObject.get("name").getAsString());
            languagePack.setAuthor(jsonObject.get("author").getAsString());
            languagePack.setVersion(jsonObject.get("version").getAsString());

            JsonObject translations = jsonObject.getAsJsonObject("translations");
            for (Map.Entry<String, JsonElement> entry : translations.entrySet()) {
                languagePack.getTranslations().put(entry.getKey(), entry.getValue().getAsString());
            }

            return languagePack;
        }

        @Override
        public JsonElement serialize(LanguagePack src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", src.getId());
            jsonObject.addProperty("name", src.getName());
            jsonObject.addProperty("author", src.getAuthor());
            jsonObject.addProperty("version", src.getVersion());

            JsonObject translations = new JsonObject();

            for (Map.Entry<String, String> entry : src.getTranslations().entrySet()) {
                translations.addProperty(entry.getKey(), entry.getValue());
            }

            jsonObject.add("translations", translations);

            return jsonObject;
        }
    }
}
