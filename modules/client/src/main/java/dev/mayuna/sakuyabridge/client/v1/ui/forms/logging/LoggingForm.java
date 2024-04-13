package dev.mayuna.sakuyabridge.client.v1.ui.forms.logging;

import dev.mayuna.sakuyabridge.client.v1.Main;
import dev.mayuna.sakuyabridge.client.v1.configs.LoggerConfig;
import dev.mayuna.sakuyabridge.commons.v2.ExceptionUtils;
import org.apache.logging.log4j.core.LogEvent;

import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
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
    protected void onCheckBoxWrapLinesChecked(ActionEvent actionEvent) {
        Main.getConfigs().getLoggerConfig().setLoggingWindowWrapLines(chckbox_wrapLines.isSelected());
        txtarea_logs.setLineWrap(chckbox_wrapLines.isSelected());
    }

    @Override
    protected void onSliderFontSizeChanged(ChangeEvent changeEvent) {
        Main.getConfigs().getLoggerConfig().setLoggingWindowFontSize(slider_fontSize.getValue());
        txtarea_logs.setFont(txtarea_logs.getFont().deriveFont((float)slider_fontSize.getValue()));
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void openForm() {
        LoggerConfig loggerConfig = Main.getConfigs().getLoggerConfig();

        chckbox_wrapLines.setSelected(loggerConfig.isLoggingWindowWrapLines());
        txtarea_logs.setFont(txtarea_logs.getFont().deriveFont(loggerConfig.getLoggingWindowFontSize()));
        slider_fontSize.setValue((int)loggerConfig.getLoggingWindowFontSize());

        super.openForm();
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
