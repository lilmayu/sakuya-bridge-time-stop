package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components;

import dev.mayuna.cinnamonroll.TabbedPanel;

import java.awt.*;

/**
 * Abstract class for the design of a tabbed panel in SakuyaBridge.
 */
public abstract class SakuyaBridgeTabbedPanelDesign extends TabbedPanel implements Translatable {

    public SakuyaBridgeTabbedPanelDesign(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);

        prepareComponents();
        registerListeners();
        populatePanel();
    }

    public SakuyaBridgeTabbedPanelDesign(LayoutManager layout) {
        this(layout, true);
    }

    public SakuyaBridgeTabbedPanelDesign(boolean isDoubleBuffered) {
        this(new FlowLayout(), isDoubleBuffered);
    }

    public SakuyaBridgeTabbedPanelDesign() {
        this(true);
    }

    /**
     * Prepares the components of the tabbed panel.
     */
    protected abstract void prepareComponents();

    /**
     * Registers the listeners of the tabbed panel.
     */
    protected abstract void registerListeners();

    /**
     * Populates the panel with the components.
     */
    protected abstract void populatePanel();
}
