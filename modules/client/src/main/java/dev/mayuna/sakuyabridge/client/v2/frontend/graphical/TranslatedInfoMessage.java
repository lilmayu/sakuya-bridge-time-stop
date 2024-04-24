package dev.mayuna.sakuyabridge.client.v2.frontend.graphical;

import dev.mayuna.cinnamonroll.extension.messages.InfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import lombok.NonNull;

/**
 * Translated info message
 */
public class TranslatedInfoMessage extends InfoMessage {

    /**
     * Creates a new translated info message
     *
     * @param title   The title of the message
     * @param message The message
     */
    public TranslatedInfoMessage(String title, @NonNull String message) {
        super(title, message);
    }

    /**
     * Creates a new translated info message
     *
     * @param message The message
     */
    public TranslatedInfoMessage(@NonNull String message) {
        super(message);
    }

    /**
     * Creates a new translated info message
     *
     * @param title   The title of the message
     * @param message The message
     */
    public static TranslatedInfoMessage create(String title, String message) {
        return new TranslatedInfoMessage(title, message);
    }

    /**
     * Creates a new translated info message
     *
     * @param message The message
     *
     * @return The translated info message
     */
    public static TranslatedInfoMessage create(String message) {
        return new TranslatedInfoMessage(message);
    }

    @Override
    public String getErrorTitle() {
        return LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_ERROR);
    }

    @Override
    public String getWarningTitle() {
        return LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_WARNING);
    }

    @Override
    public String getInfoTitle() {
        return LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_INFORMATION);
    }

    @Override
    public String getQuestionTitle() {
        return LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_QUESTION);
    }
}
