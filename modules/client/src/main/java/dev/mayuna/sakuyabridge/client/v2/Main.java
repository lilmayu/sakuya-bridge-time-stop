package dev.mayuna.sakuyabridge.client.v2;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.UserInterface;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpClientBundle;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpDestination;
import lombok.SneakyThrows;

import java.net.InetAddress;

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

    @SneakyThrows
    private static void testStuff() {
        UdpClientBundle udpClientBundle = new UdpClientBundle(
                UdpDestination.of(InetAddress.getByName("127.0.0.2"), 28088),
                UdpDestination.of(InetAddress.getByName("127.0.0.1"), 10800)
        );

        udpClientBundle.startClients();

        // Connect
        //udpClient.connect();

        // Send a message
        System.out.println("Sending dummy message to remote...");
        udpClientBundle.getRemoteClient().sendDummy();

        Thread.sleep(1000000);
    }
}
