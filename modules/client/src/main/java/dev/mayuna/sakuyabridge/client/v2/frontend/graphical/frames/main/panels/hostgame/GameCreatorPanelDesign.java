package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.hostgame;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Design for the game creator panel
 */
public abstract class GameCreatorPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTextField textFieldGameName;
    protected JComboBox<GameInfo.Region> comboBoxRegion;
    protected JComboBox<GameInfo.Version> comboBoxGameVersion;
    protected JComboBox<GameInfo.Technology> comboBoxGameTechnology;
    protected JComboBox<GameInfo.PlayerSide> comboBoxGamePlayerSide;
    protected JCheckBox checkBoxGamePrivate;
    protected JCheckBox checkBoxGamePasswordProtected;
    protected JPasswordField textFieldGamePassword;
    protected JCheckBox checkBoxDynamicPingStrategy;
    protected JCheckBox checkBoxUdpHolePunching;

    protected JButton buttonCreateGame;

    public GameCreatorPanelDesign() {
        super(MigLayoutUtils.create("[grow]"));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Game Creator")
        ));

        this.setPreferredSize(new Dimension(300, 200));

        loadStaticData();
    }

    @Override
    protected void prepareComponents() {
        comboBoxRegion = new JComboBox<>();
        comboBoxGameVersion = new JComboBox<>();
        comboBoxGameTechnology = new JComboBox<>();
        comboBoxGamePlayerSide = new JComboBox<>();
        checkBoxGamePrivate = new JCheckBox("Private Game");
        checkBoxGamePasswordProtected = new JCheckBox("Password protected");
        buttonCreateGame = new JButton("Create Game");

        textFieldGameName = new JTextField();
        CinnamonRoll.limitDocumentLength(textFieldGameName, CommonConstants.MAX_GAME_NAME_LENGTH);
        CinnamonRollFlatLaf.addDynamicOutlineError(textFieldGameName, () -> !isGameNameValid());

        textFieldGamePassword = new JPasswordField();
        CinnamonRoll.limitDocumentLength(textFieldGamePassword, CommonConstants.MAX_GAME_NAME_LENGTH);
        CinnamonRollFlatLaf.addDynamicOutlineError(textFieldGamePassword, () -> !isPasswordValid());
        CinnamonRollFlatLaf.showPasswordRevealButton(textFieldGamePassword, true);

        checkBoxDynamicPingStrategy = new JCheckBox("Dynamic ping strategy");
        checkBoxDynamicPingStrategy.setToolTipText("Sakuya Bridge will test different strategies to ensure lowest latency between players.");

        checkBoxUdpHolePunching = new JCheckBox("Direct communication");
        checkBoxUdpHolePunching.setToolTipText("""
                                                       Enables UDP hole punching for direct communication between players.
                                                       It is recommended to leave this option enabled for lowest possible latency.
                                                       Disable only in case of bad connection.
                                                       If disabled, Dynamic ping strategy won't use UDP hole punching.""");
    }

    protected abstract boolean isGameNameValid();

    protected abstract boolean isPasswordValid();

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonCreateGame, this::clickCreateGame);
        checkBoxGamePasswordProtected.addActionListener(this::checkGamePasswordProtected);

        comboBoxRegion.addActionListener(this::updateComponentSettings);
        comboBoxGameVersion.addActionListener(this::updateComponentSettings);
        comboBoxGameTechnology.addActionListener(this::updateComponentSettings);
        comboBoxGamePlayerSide.addActionListener(this::updateComponentSettings);
        checkBoxGamePrivate.addActionListener(this::updateComponentSettings);
        checkBoxDynamicPingStrategy.addActionListener(this::updateComponentSettings);
        checkBoxUdpHolePunching.addActionListener(this::updateComponentSettings);
        CinnamonRoll.onValueChanged(textFieldGameName, this::updateDocumentSettings);
        CinnamonRoll.onValueChanged(textFieldGamePassword, this::updateDocumentSettings);
        textFieldGameName.addActionListener(this::updateComponentSettings);
        textFieldGamePassword.addActionListener(this::updateComponentSettings);
    }

    protected abstract void clickCreateGame(MouseEvent mouseEvent);

    protected abstract void checkGamePasswordProtected(ActionEvent event);

    protected abstract void updateComponentSettings(ActionEvent event);

    protected abstract void updateDocumentSettings(DocumentEvent documentEvent);

    @Override
    protected void populatePanel() {
        this.add(new JLabel("Game Name"), "split 2, growx");
        this.add(CinnamonRoll.createDynamicCharacterCountLabel(textFieldGameName, true), "wrap");
        this.add(textFieldGameName, "growx, wrap");

        this.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        // Set width to 135px, half of the panel
        this.add(new JLabel("Your Region"), "growx, split 2, w 135px");
        this.add(new JLabel("Game"), "growx, wrap, w 135px");
        this.add(comboBoxRegion, "growx, split 2, w 135px");
        this.add(comboBoxGameVersion, "growx, wrap, w 135px");

        this.add(new JLabel("Technology"), "growx, split 2, w 135px");
        this.add(new JLabel("Player side"), "growx, wrap, w 135px");
        this.add(comboBoxGameTechnology, "growx, split 2, w 135px");
        this.add(comboBoxGamePlayerSide, "growx, wrap, w 135px");

        this.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        this.add(checkBoxGamePrivate, "growx, wrap");
        this.add(checkBoxGamePasswordProtected, "split 2, growx");
        this.add(CinnamonRoll.createDynamicCharacterCountLabel(textFieldGamePassword, true), "wrap");
        this.add(textFieldGamePassword, "growx, wrap");

        this.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        this.add(checkBoxDynamicPingStrategy, "wrap");
        this.add(checkBoxUdpHolePunching, "wrap");

        this.add(CinnamonRoll.horizontalSeparator(), "growx, wrap");

        this.add(buttonCreateGame, "growx, wrap");
    }

    /**
     * Load static data
     */
    private void loadStaticData() {
        // TODO: Translation for enums

        for (GameInfo.Region region : GameInfo.Region.values()) {
            comboBoxRegion.addItem(region);
        }

        for (GameInfo.Version version : GameInfo.Version.values()) {
            comboBoxGameVersion.addItem(version);
        }

        for (GameInfo.Technology technology : GameInfo.Technology.values()) {
            comboBoxGameTechnology.addItem(technology);
        }

        for (GameInfo.PlayerSide playerSide : GameInfo.PlayerSide.values()) {
            comboBoxGamePlayerSide.addItem(playerSide);
        }
    }

    /**
     * Loads data
     */
    protected abstract void loadData();

    @Override
    public void onClose() {
        // Not used
    }
}
