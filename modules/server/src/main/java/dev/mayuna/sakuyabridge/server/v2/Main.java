package dev.mayuna.sakuyabridge.server.v2;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpServer;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpServerBridge;
import lombok.SneakyThrows;

public class Main {

    public static void main(String[] args) {
        testStuff();

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

    @SneakyThrows
    private static void testStuff() {
        UdpServerBridge udpServer = new UdpServerBridge(28088, 30_000);
        udpServer.start();

        Thread.sleep(1000000);
    }
}
