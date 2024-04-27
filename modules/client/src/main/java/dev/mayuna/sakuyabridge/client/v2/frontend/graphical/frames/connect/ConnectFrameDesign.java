package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.connect;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.ClientConstants;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Design for the connect frame<br>
 */
public abstract class ConnectFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JButton buttonExit;
    protected JButton buttonConnect;
    protected JButton buttonOffline;

    protected JTextField fieldServerAddress;

    protected JLabel labelAuthor;
    protected JLabel labelVersion;

    public ConnectFrameDesign() {
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.General.TEXT_TITLE));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new MigLayout("insets n n 0 n", "[grow]")); // bottom insets 0

        prepareTitle();
        prepareServerConnect();
        prepareFooter();

        this.pack();
        this.setMinimumSize(new Dimension(340, this.getHeight()));
        this.setLocationRelativeTo(parentComponent);
    }

    @Override
    protected void prepareComponents() {
        this.buttonExit = new JButton($getTranslation(Lang.General.TEXT_EXIT));
        this.buttonConnect = new JButton($getTranslation(Lang.Frames.Connect.BUTTON_CONNECT));
        this.buttonOffline = new JButton($getTranslation(Lang.Frames.Connect.BUTTON_OFFLINE));

        this.fieldServerAddress = new JTextField();
        this.fieldServerAddress.setFont(CinnamonRoll.createMonospacedFont(12));
        CinnamonRollFlatLaf.setTextPlaceholder(fieldServerAddress, $getTranslation(Lang.Frames.Connect.PLACEHOLDER_SERVER_ADDRESS));

        this.labelAuthor = new JLabel($formatTranslation(Lang.Frames.Connect.LABEL_AUTHOR, ClientConstants.AUTHOR_NAME));
        this.labelAuthor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.labelAuthor.setToolTipText($getTranslation(Lang.Frames.Connect.WEBSITE_TOOLTIP));

        this.labelVersion = new JLabel("<VERSION>");
        this.labelVersion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.labelVersion.setToolTipText($getTranslation(Lang.Frames.Connect.RELEASE_TOOLTIP));
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonConnect, this::clickConnect);
        CinnamonRoll.onClick(buttonExit, this::clickExit);
        CinnamonRoll.onClick(labelAuthor, this::clickAuthor);
        CinnamonRoll.onClick(labelVersion, this::clickVersion);
    }

    protected abstract void clickConnect(MouseEvent mouseEvent);

    protected abstract void clickExit(MouseEvent mouseEvent);

    protected abstract void clickAuthor(MouseEvent mouseEvent);

    protected abstract void clickVersion(MouseEvent mouseEvent);

    /**
     * Prepare the title of the frame.
     */
    private void prepareTitle() {
        JLabel mainTitle = new JLabel($getTranslation(Lang.Frames.Connect.TEXT_LABEL_TITLE));
        CinnamonRoll.deriveFontWith(mainTitle, Font.BOLD, 24);
        this.add(mainTitle, "center, wrap");

        JLabel subTitle = new JLabel($getTranslation(Lang.Frames.Connect.LABEL_SUBTITLE));
        CinnamonRoll.deriveFontWith(subTitle, Font.PLAIN, 16);
        this.add(subTitle, "center, wrap, gapbottom 3");

        JLabel description = new JLabel($getTranslation(Lang.Frames.Connect.LABEL_DESCRIPTION));
        CinnamonRoll.deriveFontWith(description, Font.ITALIC, 14);
        this.add(description, "center, wrap, gapbottom 3");

        this.add(new JSeparator(), "growx, wrap");
    }

    /**
     * Prepare the server connect panel.
     */
    private void prepareServerConnect() {
        JPanel serverConnectPanel = new JPanel();
        serverConnectPanel.setLayout(new MigLayout("insets 0 n n n", "[shrink][grow][shrink][shrink]")); // top insets 0

        JLabel serverAddressLabel = new JLabel($getTranslation(Lang.Frames.Connect.LABEL_SERVER_ADDRESS));
        CinnamonRoll.makeForegroundDarker(serverAddressLabel);
        serverConnectPanel.add(serverAddressLabel, "span 4, wrap");

        serverConnectPanel.add(fieldServerAddress, "span 4, growx, wrap");

        serverConnectPanel.add(buttonExit);
        serverConnectPanel.add(new JLabel(), "growx");
        serverConnectPanel.add(buttonOffline);
        serverConnectPanel.add(buttonConnect);

        this.add(serverConnectPanel, "growx, wrap");
    }

    /**
     * Prepare the footer of the frame.
     */
    private void prepareFooter() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(MigLayoutUtils.createNoInsets("[grow][shrink]"));

        CinnamonRoll.deriveFontWith(labelAuthor, Font.PLAIN, 12);
        footerPanel.add(labelAuthor);

        CinnamonRoll.deriveFontWith(labelVersion, Font.PLAIN, 12);
        footerPanel.add(labelVersion);

        this.add(footerPanel, "growx, wrap");
    }
}
