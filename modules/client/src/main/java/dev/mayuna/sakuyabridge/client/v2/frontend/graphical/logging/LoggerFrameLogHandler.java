package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.logging;

import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.commons.v2.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Appender for the logging frame
 */
@Plugin(name = "LoggerFrameLogHandler", category = "Core", elementType = "appender", printObject = true)
public final class LoggerFrameLogHandler extends AbstractAppender {

    public static final LoggerFrameLogHandler INSTANCE = new LoggerFrameLogHandler(Level.INFO.intLevel()); // Default to INFO
    public static final LoggerTableModel TABLE_MODEL = INSTANCE.createTableModel();
    //public static final LoggerTableRenderer TABLE_RENDERER = new LoggerTableRenderer();

    private final @Getter List<LogEventCopy> logEvents = new LinkedList<>();
    private @Setter int minimalLogLevel;

    /**
     * Creates a new instance of the handler's appender
     *
     * @param minimalLogLevel The minimal level to log
     */
    private LoggerFrameLogHandler(int minimalLogLevel) {
        super("SwingLoggerFormAppender", null, null, false, null);
        this.minimalLogLevel = minimalLogLevel;
    }

    @Override
    public void append(LogEvent event) {
        // Check if the event level is higher than the minimal level
        if (event.getLevel().intLevel() > minimalLogLevel) {
            return;
        }

        // Add the event to the list
        synchronized (logEvents) {
            logEvents.add(new LogEventCopy(event));
        }
    }

    /**
     * Clears the log events
     */
    public void clearLogEvents() {
        synchronized (logEvents) {
            logEvents.clear();
        }
    }

    /**
     * Creates a table model for the logger
     *
     * @return The table model
     */
    private LoggerTableModel createTableModel() {
        return new LoggerTableModel(logEvents);
    }

    /**
     * Helper class to copy log events
     */
    @Getter
    public static final class LogEventCopy {

        private final long timeMillis;
        private final Level level;
        private final String loggerName;
        private final String message;

        /**
         * Creates a new instance of the log event copy
         *
         * @param logEvent The log event
         */
        public LogEventCopy(LogEvent logEvent) {
            this.timeMillis = logEvent.getTimeMillis();
            this.level = logEvent.getLevel();
            this.loggerName = logEvent.getLoggerName().substring(logEvent.getLoggerName().lastIndexOf('.') + 1);

            if (logEvent.getThrown() == null) {
                this.message = logEvent.getMessage().getFormattedMessage();
            } else {
                this.message = logEvent.getMessage().getFormattedMessage() + " " + ExceptionUtils.dumpException(logEvent.getThrown());
            }
        }
    }

    /**
     * Table model for the logger
     */
    public static final class LoggerTableModel extends AbstractTableModel {

        public final static int TIME_COLUMN_INDEX = 0;
        public final static int LEVEL_COLUMN_INDEX = 1;
        public final static int SOURCE_COLUMN_INDEX = 2;
        public final static int MESSAGE_COLUMN_INDEX = 3;

        private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

        private final List<LogEventCopy> logEvents;

        public LoggerTableModel(List<LogEventCopy> logEvents) {
            super();
            this.logEvents = logEvents;
        }

        @Override
        public int getRowCount() {
            synchronized (logEvents) {
                return logEvents.size();
            }
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case TIME_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Logger.COLUMN_TIME);
                case LEVEL_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Logger.COLUMN_LEVEL);
                case SOURCE_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Logger.COLUMN_SOURCE);
                case MESSAGE_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Logger.COLUMN_MESSAGE);
                default -> "Unknown column (bug)";
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            synchronized (logEvents) {
                // Check if the row index is valid
                if (rowIndex >= logEvents.size()) {
                    return null;
                }

                // Get the log event
                LogEventCopy event = logEvents.get(rowIndex);

                return switch (columnIndex) {
                    case TIME_COLUMN_INDEX -> DATE_FORMAT.format(event.getTimeMillis());
                    case LEVEL_COLUMN_INDEX -> event.getLevel().name();
                    case SOURCE_COLUMN_INDEX -> event.getLoggerName();
                    case MESSAGE_COLUMN_INDEX -> event.getMessage();
                    default -> null;
                };
            }
        }
    }

    /**
     * Table renderer for the logger
     */
    public static final class LoggerTableRenderer extends DefaultTableCellRenderer {

        public static final Color COLOR_FOREGROUND_ERROR = new Color(255, 0, 0);
        public static final Color COLOR_FOREGROUND_WARNING = new Color(255, 255, 0);
        public static final Color COLOR_FOREGROUND_SUCCESS = new Color(0, 255, 0);
        public static final Color COLOR_FOREGROUND_FLOW = new Color(83, 110, 218);
        public static final Color COLOR_FOREGROUND_DEBUG = new Color(27, 34, 64);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Check if the value is a string
            if (value instanceof String text) {
                // Set the color based on the level
                if (column == LoggerTableModel.LEVEL_COLUMN_INDEX) {
                    switch (text) {
                        case "ERROR" -> component.setForeground(COLOR_FOREGROUND_ERROR);
                        case "WARN" -> component.setForeground(COLOR_FOREGROUND_WARNING);
                        case "SUCCESS" -> component.setForeground(COLOR_FOREGROUND_SUCCESS);
                        case "FLOW" -> component.setForeground(COLOR_FOREGROUND_FLOW);
                        case "DEBUG", "MDEBUG" -> component.setForeground(COLOR_FOREGROUND_DEBUG);
                        default -> component.setForeground(table.getForeground());
                    }

                    // Return the component
                    return component;
                }
            }


            // Reset the color
            component.setForeground(table.getForeground());
            return component;
        }
    }
}
