package dev.mayuna.sakuyabridge.client.v2.frontend.frames.main.panels;

import dev.mayuna.sakuyabridge.client.v2.frontend.TabbedPanel;

public class ServerBrowserTabbedPanel extends TabbedPanel {

    @Override
    public void onOpen() {
        System.out.println("Opened server browser tabbed panel");
    }

    @Override
    public void onClose() {
        System.out.println("Closed server browser tabbed panel");
    }
}
