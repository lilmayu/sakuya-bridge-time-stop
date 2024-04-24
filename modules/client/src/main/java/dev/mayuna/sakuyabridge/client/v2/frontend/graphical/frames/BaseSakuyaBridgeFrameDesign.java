package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames;

import dev.mayuna.cinnamonroll.BaseFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;

import java.awt.*;

public abstract class BaseSakuyaBridgeFrameDesign extends BaseFrameDesign {

    /**
//     * {@inheritDoc}
     */
    public BaseSakuyaBridgeFrameDesign() {
    }

    /**
     * {@inheritDoc}
     */
    public BaseSakuyaBridgeFrameDesign(Component parentComponent) {
        super(parentComponent);
    }

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
