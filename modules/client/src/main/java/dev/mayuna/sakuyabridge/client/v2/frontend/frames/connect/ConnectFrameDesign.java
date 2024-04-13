package dev.mayuna.sakuyabridge.client.v2.frontend.frames.connect;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.util.DesignUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.util.DesktopUtils;
import dev.mayuna.sakuyabridge.client.v2.MiscConstants;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class ConnectFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JButton buttonExit;
    protected JButton buttonConnect;
    protected JButton buttonOffline;

    protected JTextField serverAddressField;

    protected JLabel labelAuthor;
    protected JLabel labelVersion;

    public ConnectFrameDesign() {
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.General.TITLE));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new MigLayout("insets n n 0 n", "[grow]")); // bottom insets 0

        prepareTitle();
        prepareServerConnect();
        prepareFooter();

        this.pack();
        this.setMinimumSize(new Dimension(340, this.getHeight()));
        this.setLocationRelativeTo(parentComponent);

        this.getContentPane().requestFocusInWindow();
    }

    @Override
    protected void prepareComponents() {
        this.buttonExit = new JButton($getTranslation(Lang.Frames.Connect.BUTTON_EXIT));
        this.buttonConnect = new JButton($getTranslation(Lang.Frames.Connect.BUTTON_CONNECT));
        this.buttonOffline = new JButton($getTranslation(Lang.Frames.Connect.BUTTON_OFFLINE));

        this.serverAddressField = new JTextField();
        this.serverAddressField.setFont(DesignUtils.createMonospacedFont(12));
        DesignUtils.setPlaceholder(serverAddressField, $getTranslation(Lang.Frames.Connect.PLACEHOLDER_SERVER_ADDRESS));

        this.labelAuthor = new JLabel($formatTranslation(Lang.Frames.Connect.LABEL_AUTHOR, MiscConstants.AUTHOR_NAME));
        this.labelAuthor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.labelAuthor.setToolTipText($getTranslation(Lang.Frames.Connect.WEBSITE_TOOLTIP));

        this.labelVersion = new JLabel("<VERSION>");
        this.labelVersion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.labelVersion.setToolTipText($getTranslation(Lang.Frames.Connect.RELEASE_TOOLTIP));
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonConnect, this::clickConnect);
        CinnamonRoll.onClick(buttonExit, e -> System.exit(0));
        CinnamonRoll.onClick(labelAuthor, e -> DesktopUtils.openUrl(MiscConstants.MAYUNA_WEBSITE_URL));
        CinnamonRoll.onClick(labelVersion, e -> DesktopUtils.openUrl(MiscConstants.GITHUB_RELEASE_URL));
    }

    protected abstract void clickConnect(MouseEvent mouseEvent);

    protected abstract void openLoggingFrame(MouseEvent mouseEvent);

    /**
     * Prepare the title of the frame.
     */
    private void prepareTitle() {
        JLabel mainTitle = new JLabel($getTranslation(Lang.Frames.Connect.LABEL_TITLE));
        DesignUtils.deriveFontWith(mainTitle, Font.BOLD, 24);
        this.add(mainTitle, "center, wrap");

        JLabel subTitle = new JLabel($getTranslation(Lang.Frames.Connect.LABEL_SUBTITLE));
        DesignUtils.deriveFontWith(subTitle, Font.PLAIN, 16);
        this.add(subTitle, "center, wrap, gapbottom 3");

        JLabel description = new JLabel($getTranslation(Lang.Frames.Connect.LABEL_DESCRIPTION));
        DesignUtils.deriveFontWith(description, Font.ITALIC, 12);
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
        serverAddressLabel.setForeground(serverAddressLabel.getForeground().darker());
        serverConnectPanel.add(serverAddressLabel, "span 4, wrap");

        serverConnectPanel.add(serverAddressField, "span 4, growx, wrap");

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

        DesignUtils.deriveFontWith(labelAuthor, Font.PLAIN, 12);
        footerPanel.add(labelAuthor);

        DesignUtils.deriveFontWith(labelVersion, Font.PLAIN, 12);
        footerPanel.add(labelVersion);

        this.add(footerPanel, "growx, wrap");
    }
}
