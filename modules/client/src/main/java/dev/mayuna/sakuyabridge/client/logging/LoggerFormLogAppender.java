package dev.mayuna.sakuyabridge.client.logging;

import dev.mayuna.sakuyabridge.client.ui.forms.logging.LoggingForm;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "SwingLoggerFormAppender", category = "Core", elementType = "appender", printObject = true)
public class LoggerFormLogAppender extends AbstractAppender {

    private final Level minimalLevel;

    public LoggerFormLogAppender(Level minimalLevel) {
        super("SwingLoggerFormAppender", null, null, false, null);
        this.minimalLevel = minimalLevel;
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().intLevel() > minimalLevel.intLevel()) {
            return;
        }

        LoggingForm.getInstance().appendLogEvent(event);
    }
}
