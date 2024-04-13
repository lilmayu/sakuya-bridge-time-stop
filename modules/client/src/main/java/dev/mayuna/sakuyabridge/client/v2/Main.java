package dev.mayuna.sakuyabridge.client.v2;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.SakuyaBridgeGraphicalInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.SakuyaBridgeInterface;

public class Main {

    private static final SakuyaBridgeInterface sakuyaBridgeInterface = SakuyaBridgeGraphicalInterface.INSTANCE;

    /**
     * Main method
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        // Hooking the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(Main::exit));

        // Boot the SakuyaBridge
        SakuyaBridge.INSTANCE.boot();

        // TODO: Ability to change interface using args
        sakuyaBridgeInterface.start();
    }

    /**
     * Exits the SakuyaBridge
     */
    private static void exit() {
        // Reset the SakuyaBridge
        SakuyaBridge.INSTANCE.reset();

        // Stop the interface
        sakuyaBridgeInterface.stop();
    }
}
