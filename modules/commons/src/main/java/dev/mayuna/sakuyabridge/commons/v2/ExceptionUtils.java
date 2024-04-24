package dev.mayuna.sakuyabridge.commons.v2;

import dev.mayuna.sakuyabridge.commons.v2.jacoco.Generated;

/**
 * Exception utilities
 */
@Generated
public final class ExceptionUtils {

    /**
     * Dumps exception to string
     *
     * @param throwable Throwable
     *
     * @return String
     */
    public static StringBuilder dumpException(Throwable throwable) {
        return appendThrowable(new StringBuilder(), throwable);
    }

    /**
     * Appends throwable to string
     *
     * @param text      Text
     * @param throwable Throwable
     *
     * @return String
     */
    private static StringBuilder appendThrowable(StringBuilder text, Throwable throwable) {
        text.append("\n").append(throwable.toString());
        for (StackTraceElement element : throwable.getStackTrace()) {
            text.append("\n\tat ").append(element.toString());
        }

        // Append cause stacktrace
        if (throwable.getCause() != null) {
            return appendThrowable(text.append("\n-> Caused by: "), throwable.getCause());
        }

        return text;
    }
}
