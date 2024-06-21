package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.logger.info;

import dev.mayuna.cinnamonroll.util.ClipboardUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public final class LogInfoFrame extends LogInfoFrameDesign {

    /**
     * Open the log info frame.
     *
     * @param parent     The parent component.
     * @param logTime    The log time.
     * @param logLevel   The log level.
     * @param logSource  The log source.
     * @param logMessage The log message.
     */
    public LogInfoFrame(Component parent, String logTime, String logLevel, String logSource, String logMessage) {
        super(parent, logTime, logLevel, logSource, logMessage);
    }

    @Override
    protected void clickExit(MouseEvent mouseEvent) {
        this.dispose();
    }

    @Override
    protected void clickCopy(MouseEvent mouseEvent) {
        ClipboardUtils.setContent(logTime + "\t" + logLevel + "\t" + logSource + "\t" + logMessage);

        TranslatedInfoMessage.create($getTranslation(Lang.Frames.LogInfo.TEXT_LOG_COPIED_TO_CLIPBOARD)).showInfo();
    }

    @Override
    public void onEscapePressed(ActionEvent event) {
        dispose();
    }
}
