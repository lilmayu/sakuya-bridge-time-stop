package dev.mayuna.sakuyabridge.client.v2.frontend.util;

public class StringUtils {

    /**
     * Format a string with indexed arguments.
     *
     * @param format The format string
     * @param args   The arguments
     *
     * @return The formatted string
     */
    public static String indexedFormat(String format, Object... args) {
        for (int i = 0; i < args.length; i++) {
            format = format.replace("{" + i + "}", args[i].toString());
        }

        return format;
    }

}
