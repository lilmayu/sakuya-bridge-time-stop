package dev.mayuna.sakuyabridge.client.v2.frontend.util;

import javax.swing.*;
import java.awt.*;

// TODO: Přidat do CinnamonRollu
public class DesignUtils {

    private static final Font monospacedFontCache = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    /**
     * Derive the font of a component with a new style and size.
     *
     * @param component The component to derive the font of.
     * @param style     The new style.
     * @param size      The new size.
     */
    public static void deriveFontWith(JComponent component, int style, int size) {
        component.setFont(component.getFont().deriveFont(style, size));
    }

    /**
     * Create a monospaced font with the specified size.
     *
     * @param size The size of the font.
     *
     * @return The created font.
     */
    public static Font createMonospacedFont(int size) {
        return monospacedFontCache.deriveFont(Font.PLAIN, size);
    }

    // TODO: Client properties brát z FlatClientProperties

    /**
     * Set a placeholder for a text field or other supported component. (JTextField.placeholderText)
     *
     * @param component   The text field.
     * @param placeholder The placeholder.
     */
    public static void setPlaceholder(JComponent component, String placeholder) {
        component.putClientProperty("JTextField.placeholderText", placeholder);
    }

    /**
     * Set the tab type of tabbed pane. (JTabbedPane.tabType)
     *
     * @param tabbedPane The tabbed pane.
     * @param tabType    The tab type.
     */
    public static void setTabType(JTabbedPane tabbedPane, TabType tabType) {
        tabbedPane.putClientProperty("JTabbedPane.tabType", tabType.getClientPropertyValue());
    }

    /**
     * Show or hide the tab separator of a tabbed pane. (JTabbedPane.showTabSeparator)
     *
     * @param tabbedPane The tabbed pane.
     * @param show       Whether to show the tab separator.
     */
    public static void showTabSeparator(JTabbedPane tabbedPane, boolean show) {
        tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", show);
    }

    /**
     * Show or hide the border around the tabbed pane. (JTabbedPane.hasFullBorder)
     *
     * @param tabbedPane The tabbed pane.
     * @param show       Whether to show the border.
     */
    public static void showBorder(JTabbedPane tabbedPane, boolean show) {
        tabbedPane.putClientProperty("JTabbedPane.hasFullBorder", show);
    }

    public enum TabType {
        CARD,
        UNDERLINED;

        /**
         * Get the client property value for the tab type.
         *
         * @return The client property value.
         */
        public String getClientPropertyValue() {
            if (this == CARD) {
                return "card";
            }

            return null;
        }
    }
}
