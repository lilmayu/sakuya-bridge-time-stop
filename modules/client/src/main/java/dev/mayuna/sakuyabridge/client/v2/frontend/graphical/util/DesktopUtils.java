package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;


// TODO: Přidat do CinnamonRoll?
public class DesktopUtils {

    /**
     * Open a URL in the default browser.
     *
     * @param url The URL to open.
     */
    public static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ignored) {
        }
    }

}
