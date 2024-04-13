package dev.mayuna.sakuyabridge.client.v2.frontend.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

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
