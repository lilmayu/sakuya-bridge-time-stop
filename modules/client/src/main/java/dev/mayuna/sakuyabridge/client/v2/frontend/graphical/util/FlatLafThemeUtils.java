package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.util;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;

public class FlatLafThemeUtils {

    /**
     * Get the name of the look and feel with a prefix indicating whether it is dark or light
     *
     * @param lookAndFeelInfo The look and feel info
     *
     * @return The name with prefix
     */
    public static String getNameWithPrefix(FlatAllIJThemes.FlatIJLookAndFeelInfo lookAndFeelInfo) {
        return (lookAndFeelInfo.isDark() ? LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_THEME_DARK) : LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_THEME_LIGHT)) + " " + lookAndFeelInfo.getName();
    }
}
