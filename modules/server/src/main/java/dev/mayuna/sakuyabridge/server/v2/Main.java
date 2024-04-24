package dev.mayuna.sakuyabridge.server.v2;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;

public class Main {

    public static void main(String[] args) {
        // Hook the shutdown handler
        Runtime.getRuntime().addShutdownHook(new Thread(Main::exit));

        // Start the SakuyaBridge
        SakuyaBridge.INSTANCE.start();
    }

    private static void exit() {
        Thread.currentThread().setName("Shutdown");

        // Stop the SakuyaBridge
        SakuyaBridge.INSTANCE.stop();
    }
}
