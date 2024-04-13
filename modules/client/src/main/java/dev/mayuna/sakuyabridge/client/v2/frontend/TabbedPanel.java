package dev.mayuna.sakuyabridge.client.v2.frontend;

import javax.swing.*;
import java.awt.*;

// TODO: Přidat do CinnamonRollu

/**
 * A panel that can be used in a {@link JTabbedPane} that has callbacks for when the tab is opened and closed.
 */
public abstract class TabbedPanel extends JPanel {

    /**
     * The client property key for the previous tab panel.<br>
     * Type: {@link TabbedPanel}
     */
    public static final String CLIENT_PROPERTY_PREVIOUS_TAB = "TabbedPanel.previousTabPanel";

    /**
     * The client property key for if the tabbed pane is configured. Setting this to false will be treated as if it would be configured.<br>
     * Type: {@link Boolean}
     */
    public static final String CLIENT_PROPERTY_CONFIGURED = "TabbedPanel.configured";

    /**
     * Creates a new JPanel with the specified layout manager and buffering
     * strategy.
     *
     * @param layout           the LayoutManager to use
     * @param isDoubleBuffered a boolean, true for double-buffering, which
     *                         uses additional memory space to achieve fast, flicker-free
     *                         updates
     */
    public TabbedPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * Create a new buffered JPanel with the specified layout manager
     *
     * @param layout the LayoutManager to use
     */
    public TabbedPanel(LayoutManager layout) {
        this(layout, true);
    }

    /**
     * Creates a new <code>JPanel</code> with <code>FlowLayout</code>
     * and the specified buffering strategy.
     * If <code>isDoubleBuffered</code> is true, the <code>JPanel</code>
     * will use a double buffer.
     *
     * @param isDoubleBuffered a boolean, true for double-buffering, which
     *                         uses additional memory space to achieve fast, flicker-free
     *                         updates
     */
    public TabbedPanel(boolean isDoubleBuffered) {
        this(new FlowLayout(), isDoubleBuffered);
    }

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public TabbedPanel() {
        this(true);
    }

    /**
     * Configures the tabbed pane to call {@link TabbedPanel#onOpen()} and {@link TabbedPanel#onClose()} when the tab is changed.<br>
     * Configuring the tabbed pane multiple times will have no effect.
     *
     * @param tabbedPane The tabbed pane to configure
     */
    public static void configureTabbedPane(JTabbedPane tabbedPane) {
        // Check if the tabbed pane is already configured
        if (tabbedPane.getClientProperty(CLIENT_PROPERTY_CONFIGURED) != null) {
            return;
        }

        // Configure the tabbed pane
        tabbedPane.putClientProperty(CLIENT_PROPERTY_CONFIGURED, true);

        // Add a change listener
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getClientProperty(CLIENT_PROPERTY_PREVIOUS_TAB) != null) {
                var previousPanel = (TabbedPanel) tabbedPane.getClientProperty(CLIENT_PROPERTY_PREVIOUS_TAB);
                previousPanel.onClose();
                tabbedPane.putClientProperty(CLIENT_PROPERTY_PREVIOUS_TAB, null);
            }

            var changedToPanel = tabbedPane.getSelectedComponent();

            if (changedToPanel instanceof TabbedPanel) {
                tabbedPane.putClientProperty(CLIENT_PROPERTY_PREVIOUS_TAB, changedToPanel);
                ((TabbedPanel) changedToPanel).onOpen();
            }
        });
    }

    /**
     * Called when the tab is opened.
     */
    public abstract void onOpen();

    /**
     * Called when the tab is closed.
     */
    public abstract void onClose();
}
