package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.logger.info;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class LogInfoFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected String logTime;
    protected String logLevel;
    protected String logSource;
    protected String logMessage;

    protected JTextField textFieldLogTime;
    protected JTextField textFieldLogLevel;
    protected JTextField textFieldLogSource;
    protected JTextArea textAreaLogMessage;

    protected JButton buttonExit;
    protected JButton buttonCopy;

    /**
     * Open the log info frame.
     *
     * @param parent     The parent component.
     * @param logTime    The log time.
     * @param logLevel   The log level.
     * @param logSource  The log source.
     * @param logMessage The log message.
     */
    public LogInfoFrameDesign(Component parent, String logTime, String logLevel, String logSource, String logMessage) {
        super(parent);

        this.logTime = logTime;
        this.logLevel = logLevel;
        this.logSource = logSource;
        this.logMessage = logMessage;

        populateData();
    }

    /**
     * Populate the data.
     */
    private void populateData() {
        textFieldLogTime.setText(logTime);
        textFieldLogLevel.setText(logLevel);
        textFieldLogSource.setText(logSource);
        textAreaLogMessage.setText(logMessage);
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.Frames.LogInfo.TEXT_TITLE));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareBody();
        prepareFooter();

        this.pack();
        this.setSize(800, 600);
        this.setLocationRelativeTo(parentComponent);
    }

    @Override
    protected void prepareComponents() {
        textFieldLogTime = new JTextField();
        textFieldLogLevel = new JTextField();
        textFieldLogSource = new JTextField();
        textAreaLogMessage = new JTextArea();

        textFieldLogTime.setEditable(false);
        textFieldLogLevel.setEditable(false);
        textFieldLogSource.setEditable(false);
        textAreaLogMessage.setEditable(false);

        textAreaLogMessage.setFont(CinnamonRoll.createMonospacedFont(12));

        buttonExit = new JButton($getTranslation(Lang.General.TEXT_EXIT));
        buttonCopy = new JButton($getTranslation(Lang.Frames.LogInfo.BUTTON_COPY_LOG));
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonExit, this::clickExit);
        CinnamonRoll.onClick(buttonCopy, this::clickCopy);
    }

    protected abstract void clickExit(MouseEvent mouseEvent);

    protected abstract void clickCopy(MouseEvent mouseEvent);

    private void prepareBody() {
        JPanel bodyPanel = new JPanel(MigLayoutUtils.createGrow());

        JLabel labelTime = new JLabel($getTranslation(Lang.Frames.Logger.COLUMN_TIME));
        CinnamonRoll.makeForegroundDarker(labelTime);
        bodyPanel.add(labelTime, "wrap");
        bodyPanel.add(textFieldLogTime, "growx, wrap");

        JLabel labelLevel = new JLabel($getTranslation(Lang.Frames.Logger.COLUMN_LEVEL));
        CinnamonRoll.makeForegroundDarker(labelLevel);
        bodyPanel.add(labelLevel, "wrap");
        bodyPanel.add(textFieldLogLevel, "growx, wrap");

        JLabel labelSource = new JLabel($getTranslation(Lang.Frames.Logger.COLUMN_SOURCE));
        CinnamonRoll.makeForegroundDarker(labelSource);
        bodyPanel.add(labelSource, "wrap");
        bodyPanel.add(textFieldLogSource, "growx, wrap");

        bodyPanel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        JLabel labelMessage = new JLabel($getTranslation(Lang.Frames.Logger.COLUMN_MESSAGE));
        CinnamonRoll.makeForegroundDarker(labelMessage);
        bodyPanel.add(labelMessage, "wrap");
        bodyPanel.add(new JScrollPane(textAreaLogMessage), "grow, push, wrap");

        this.add(bodyPanel, "dock center");
    }

    private void prepareFooter() {
        JPanel footerPanel = new JPanel(MigLayoutUtils.create());

        footerPanel.add(buttonExit);
        footerPanel.add(CinnamonRoll.verticalSeparator(), "growy");
        footerPanel.add(buttonCopy, "wrap");

        this.add(footerPanel, "dock south");
    }
}
