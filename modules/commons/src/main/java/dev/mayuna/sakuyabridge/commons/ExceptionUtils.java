package dev.mayuna.sakuyabridge.commons;

public class ExceptionUtils {

    /**
     * Dumps exception to string
     *
     * @param throwable Throwable
     *
     * @return String
     */
    public static String dumpException(Throwable throwable) {
        return appendThrowable("", throwable);
    }

    /**
     * Appends throwable to string
     *
     * @param text      Text
     * @param throwable Throwable
     *
     * @return String
     */
    private static String appendThrowable(String text, Throwable throwable) {
        text += "\n" + throwable.toString();
        for (StackTraceElement element : throwable.getStackTrace()) {
            text += "\n\tat " + element.toString();
        }

        // Append cause stacktrace
        if (throwable.getCause() != null) {
            return appendThrowable(text + "\n-> Caused by: ", throwable.getCause());
        }

        return text;
    }
}
