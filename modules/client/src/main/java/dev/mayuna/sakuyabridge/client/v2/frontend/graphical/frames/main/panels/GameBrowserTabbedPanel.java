package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels;

import dev.mayuna.cinnamonroll.TabbedPanel;

public class GameBrowserTabbedPanel extends TabbedPanel {

    @Override
    public void onOpen() {
        System.out.println("Opened server browser tabbed panel");
    }

    @Override
    public void onClose() {
        System.out.println("Closed server browser tabbed panel");
    }
}
