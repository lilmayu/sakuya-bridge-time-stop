package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.logger;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.extension.messages.InfoMessage;
import dev.mayuna.cinnamonroll.util.ClipboardUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.logger.info.LogInfoFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.logging.LoggerFrameLogHandler;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;

/**
 * Logger frame
 */
public final class LoggerFrame extends LoggerFrameDesign {

    public static final LoggerFrame INSTANCE = new LoggerFrame();

    private LoggerFrame() {
    }

    /**
     * Opens or hides the frame
     */
    public void flipFrameVisibility() {
        this.setVisible(!this.isVisible());
    }

    /**
     * Installs the keybind for the frame
     */
    public void installKeybind() {
        CinnamonRoll.registerGlobalKeystroke("ctrl shift alt L", e -> flipFrameVisibility());
    }

    @Override
    protected void onBeforeOpened(boolean firstOpen) {
        if (firstOpen) {
            TranslatedInfoMessage.create($getTranslation(Lang.Frames.Logger.TEXT_PERSONAL_INFORMATION_WARNING)).showWarning();
        }
    }

    @Override
    public void windowActivated(WindowEvent event) {
        tableLogs.resizeColumnWidthsToFitContent();
        super.windowActivated(event);
    }

    @Override
    public void paint(Graphics g) {
        tableLogs.resizeColumnWidthsToFitContent();
        super.paint(g);
    }

    @Override
    protected void clickRowTableLogs(MouseEvent mouseEvent) {
        var selectedRow = tableLogs.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        String time = tableLogs.getValueAt(selectedRow, LoggerFrameLogHandler.LoggerTableModel.TIME_COLUMN_INDEX).toString();
        String level = tableLogs.getValueAt(selectedRow, LoggerFrameLogHandler.LoggerTableModel.LEVEL_COLUMN_INDEX).toString();
        String source = tableLogs.getValueAt(selectedRow, LoggerFrameLogHandler.LoggerTableModel.SOURCE_COLUMN_INDEX).toString();
        String message = tableLogs.getValueAt(selectedRow, LoggerFrameLogHandler.LoggerTableModel.MESSAGE_COLUMN_INDEX).toString();

        new LogInfoFrame(this, time, level, source, message).openFrame();
    }

    @Override
    protected void clickExit(MouseEvent mouseEvent) {
        this.dispose();
    }

    @Override
    protected void clickCopy(MouseEvent mouseEvent) {
        StringBuilder logs = new StringBuilder();

        for (int i = 0; i < tableLogs.getRowCount(); i++) {
            for (int j = 0; j < tableLogs.getColumnCount(); j++) {
                logs.append(tableLogs.getValueAt(i, j)).append("\t");
            }

            logs.append("\n");
        }

        ClipboardUtils.setContent(logs.toString());
        TranslatedInfoMessage.create($getTranslation(Lang.Frames.Logger.TEXT_LOGS_COPIED_TO_CLIPBOARD)).showInfo(this);
    }
}
