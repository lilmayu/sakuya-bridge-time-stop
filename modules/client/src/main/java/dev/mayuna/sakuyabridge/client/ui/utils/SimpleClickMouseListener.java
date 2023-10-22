package dev.mayuna.sakuyabridge.client.ui.utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class SimpleClickMouseListener implements MouseListener {

    private final Consumer<MouseEvent> onClick;

    public SimpleClickMouseListener(Consumer<MouseEvent> onClick) {
        this.onClick = onClick;
    }

    /**
     * Registers a {@link SimpleClickMouseListener} to a {@link Component}.
     *
     * @param component The {@link Component} to register the listener to.
     * @param onClick   The {@link Consumer} to call when the {@link Component} is clicked.
     */
    public static void register(Component component, Consumer<MouseEvent> onClick) {
        component.addMouseListener(new SimpleClickMouseListener(onClick));
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() instanceof Component component) {
            if (!component.isEnabled()) {
                return;
            }
        }

        // Check if mouse is over the component
        if (e.getX() < 0 || e.getY() < 0 || e.getX() > e.getComponent().getWidth() || e.getY() > e.getComponent().getHeight()) {
            return;
        }

        onClick.accept(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
