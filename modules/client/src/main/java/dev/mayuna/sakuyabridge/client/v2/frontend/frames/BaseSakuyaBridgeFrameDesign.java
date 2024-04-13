package dev.mayuna.sakuyabridge.client.v2.frontend.frames;

import dev.mayuna.cinnamonroll.BaseFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;

public abstract class BaseSakuyaBridgeFrameDesign extends BaseFrameDesign {

    /**
     * Get a translation from the language manager.
     *
     * @param key The key of the translation.
     *
     * @return The translation.
     */
    protected String $getTranslation(String key) {
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
    protected String $formatTranslation(String key, Object... args) {
        return LanguageManager.INSTANCE.formatTranslation(key, args);
    }
}
