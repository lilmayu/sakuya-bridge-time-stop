package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.hostgame;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;

import javax.swing.*;
import java.awt.*;

/**
 * Design for the game status panel
 */
public abstract class GameStatusPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTabbedPane tabbedPane;

    protected JTextField textFieldGameName;
    protected JTextField textFieldRegion;
    protected JTextField textFieldGameVersion;
    protected JTextField textFieldGameTechnology;
    protected JTextField textFieldGamePlayerSide;
    protected JCheckBox checkBoxGamePrivate;
    protected JCheckBox checkBoxGamePasswordProtected;
    protected JPasswordField textFieldGamePassword;
    protected JCheckBox checkBoxDynamicPingStrategy;
    protected JCheckBox checkBoxUdpHolePunching;

    protected JTextField textFieldIp;
    protected JTextField textFieldPort;
    protected JTextField textFieldGameStatus;

    protected JTextField textFieldTechnologyStatus;
    protected JTextField textFieldBridgeStatus;
    protected JLabel labelCurrentAction;
    protected JProgressBar progressBarStatus;

    protected JLabel labelBytesTransferred;

    protected JButton buttonStopGame;

    /**
     * Constructor
     */
    public GameStatusPanelDesign() {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Game Status")
        ));

        this.setPreferredSize(new Dimension(300, 200));
    }

    @Override
    protected void prepareComponents() {
        tabbedPane = new JTabbedPane();

        textFieldGameName = new JTextField();
        textFieldRegion = new JTextField();
        textFieldGameVersion = new JTextField();
        textFieldGameTechnology = new JTextField();
        textFieldGamePlayerSide = new JTextField();
        checkBoxGamePrivate = new JCheckBox("Private Game");
        checkBoxGamePasswordProtected = new JCheckBox("Password protected");
        textFieldGamePassword = new JPasswordField();
        checkBoxDynamicPingStrategy = new JCheckBox("Dynamic Ping Strategy");
        checkBoxUdpHolePunching = new JCheckBox("UDP Hole Punching");

        textFieldIp = new JTextField();
        textFieldPort = new JTextField();
        textFieldGameStatus = new JTextField();

        textFieldTechnologyStatus = new JTextField();
        textFieldBridgeStatus = new JTextField();
        labelCurrentAction = new JLabel("Loading");
        progressBarStatus = new JProgressBar();

        labelBytesTransferred = new JLabel("128 MB/36 MB");

        buttonStopGame = new JButton("Stop Game");

        textFieldGameName.setEditable(false);
        textFieldRegion.setEditable(false);
        textFieldGameVersion.setEditable(false);
        textFieldGameTechnology.setEditable(false);
        textFieldGamePlayerSide.setEditable(false);
        checkBoxGamePrivate.setEnabled(false);
        checkBoxGamePasswordProtected.setEnabled(false);
        checkBoxDynamicPingStrategy.setEnabled(false);
        checkBoxUdpHolePunching.setEnabled(false);

        textFieldIp.setEditable(false);
        textFieldPort.setEditable(false);
        textFieldGameStatus.setEditable(false);

        textFieldTechnologyStatus.setEditable(false);
        textFieldBridgeStatus.setEditable(false);

        // Password specific
        textFieldGamePassword.setEditable(false);
        CinnamonRollFlatLaf.showPasswordRevealButton(textFieldGamePassword, true);
    }

    @Override
    protected void registerListeners() {

    }

    @Override
    protected void populatePanel() {
        this.tabbedPane.addTab("Game Status", createGameStatusPanel());
        this.tabbedPane.addTab("Game Info", createGameInfoPanel());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Creates the game status panel
     *
     * @return The panel
     */
    private JPanel createGameStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(MigLayoutUtils.create("[grow]"));

        panel.add(new JLabel("IP"), "wrap");
        panel.add(textFieldIp, "growx, wrap");
        panel.add(new JLabel("Port"), "wrap");
        panel.add(textFieldPort, "growx, wrap");
        panel.add(new JLabel("Game Status"), "wrap");
        panel.add(textFieldGameStatus, "growx, wrap");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        panel.add(new JLabel("Technology Status"), "wrap");
        panel.add(textFieldTechnologyStatus, "growx, wrap");
        panel.add(new JLabel("Bridge Status"), "wrap");
        panel.add(textFieldBridgeStatus, "growx, wrap");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        panel.add(labelCurrentAction, "wrap");
        panel.add(progressBarStatus, "growx, wrap");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        panel.add(new JLabel("Bytes transferred: "), "split 2");
        panel.add(labelBytesTransferred, "growx, wrap");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        panel.add(Box.createGlue(), "split 2, growx");
        panel.add(buttonStopGame, "wrap");

        return panel;
    }

    /**
     * Creates the game info panel
     *
     * @return The panel
     */
    private JPanel createGameInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(MigLayoutUtils.create("[grow]"));

        panel.add(new JLabel("Game Name"), "wrap");
        panel.add(textFieldGameName, "growx, wrap");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        // Set width to 135px, half of the panel
        panel.add(new JLabel("Your Region"), "growx, split 2, w 135px");
        panel.add(new JLabel("Game"), "growx, wrap, w 135px");
        panel.add(textFieldRegion, "growx, split 2, w 135px");
        panel.add(textFieldGameVersion, "growx, wrap, w 135px");

        panel.add(new JLabel("Technology"), "growx, split 2, w 135px");
        panel.add(new JLabel("Player side"), "growx, wrap, w 135px");
        panel.add(textFieldGameTechnology, "growx, split 2, w 135px");
        panel.add(textFieldGamePlayerSide, "growx, wrap, w 135px");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        panel.add(checkBoxGamePrivate, "growx, wrap");
        panel.add(checkBoxGamePasswordProtected, "growx, wrap");
        panel.add(textFieldGamePassword, "growx, wrap");

        panel.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        panel.add(checkBoxDynamicPingStrategy, "growx, wrap");
        panel.add(checkBoxUdpHolePunching, "growx, wrap");

        return panel;
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose() {
    }

    /**
     * Fills the data
     *
     * @param gameInfo Game info
     */
    protected void fillData(GameInfo gameInfo) {
        textFieldGameName.setText(gameInfo.getName());
        textFieldRegion.setText(gameInfo.getRegion().toString());
        textFieldGameVersion.setText(gameInfo.getVersion().toString());
        textFieldGameTechnology.setText(gameInfo.getTechnology().toString());
        textFieldGamePlayerSide.setText(gameInfo.getPreferredPlayerSide().toString());
        checkBoxGamePrivate.setSelected(gameInfo.isPrivateGame());
        checkBoxGamePasswordProtected.setSelected(gameInfo.isPasswordProtected());
        //textFieldGamePassword.setText(gameInfo.getPas());
        checkBoxDynamicPingStrategy.setSelected(gameInfo.isDynamicPingStrategyEnabled());
        checkBoxUdpHolePunching.setSelected(gameInfo.isUdpHolePunchingEnabled());
    }

    /**
     * Sets the IP
     *
     * @param ip IP
     */
    protected void setIp(String ip) {
        textFieldIp.setText(ip);
    }

    /**
     * Sets the port
     *
     * @param port Port
     */
    protected void setPort(int port) {
        textFieldPort.setText(String.valueOf(port));
    }

    /**
     * Sets the game status
     *
     * @param status Status
     */
    protected void setGameStatus(GameInfo.Status status) {
        // TODO: Enum translation

        // FIXME: Debug
        if (status == null) {
            status = GameInfo.Status.STARTING;
        }

        textFieldGameStatus.setText(status.toString());
    }
}
