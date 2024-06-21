package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components;

import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;

/**
 * Interface for components that can be translated.
 */
public interface Translatable {

    /**
     * Get a translation from the language manager.
     *
     * @param key The key of the translation.
     *
     * @return The translation.
     */
    default String $getTranslation(String key) {
        return LanguageManager.INSTANCE.getTranslation(key);
    }

    /**
     * Format a translation with arguments.
     *
     * @param key  The key of the translation.
     * @param args The arguments to format the translation with.
     *
     * @return The formatted translation.
     */
    default String $formatTranslation(String key, Object... args) {
        return LanguageManager.INSTANCE.formatTranslation(key, args);
    }

}
