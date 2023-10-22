package dev.mayuna.sakuyabridge.client.ui.forms.logging;

import dev.mayuna.sakuyabridge.commons.ExceptionUtils;
import org.apache.logging.log4j.core.LogEvent;

import java.text.SimpleDateFormat;

public class LoggingForm extends LoggingFormDesign {

    private static LoggingForm instance;

    public static LoggingForm getInstance() {
        if (instance == null) {
            instance = new LoggingForm();
        }
        return instance;
    }

    private LoggingForm() {
        super();
    }

    @Override
    protected void loadData() {

    }

    /**
     * Appends a log event to the logging window
     *
     * @param event The log event to append
     */
    public void appendLogEvent(LogEvent event) {
        String formattedTime = new SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date(event.getTimeMillis()));
        String formattedLoggerName = event.getLoggerName().substring(event.getLoggerName().lastIndexOf(".") + 1);
        String text = "[" + formattedTime + "][" + formattedLoggerName + "][" + event.getLevel().toString() + "]: " + event.getMessage()
                                                                                                                             .getFormattedMessage();

        if (event.getThrown() != null) {
            text += ExceptionUtils.dumpException(event.getThrown());
        }

        txtarea_logs.append(text + "\n");
        txtarea_logs.setCaretPosition(txtarea_logs.getDocument().getLength());
    }
}
