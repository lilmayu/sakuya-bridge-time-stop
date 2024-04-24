package dev.mayuna.sakuyabridge.client.v2;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.UserInterface;

public class Main {

    private static final UserInterface USER_INTERFACE = GraphicalUserInterface.INSTANCE;

    /**
     * Main method
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        // Hooking the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(Main::exit));

        SakuyaBridge.INSTANCE.boot();

        // TODO: Ability to change interface using args
        USER_INTERFACE.start();
    }

    /**
     * Exits the SakuyaBridge
     */
    private static void exit() {
        // Reset the SakuyaBridge
        SakuyaBridge.INSTANCE.reset();

        // Stop the interface
        USER_INTERFACE.stop();
    }
}
