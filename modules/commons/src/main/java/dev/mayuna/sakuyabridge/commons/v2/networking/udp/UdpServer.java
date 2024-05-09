package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import lombok.Getter;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * UDP server
 */
@Getter
public abstract class UdpServer extends UdpNetworkNode {

    protected static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(UdpServer.class);

    private final int port;

    /**
     * Creates a new UDP server
     *
     * @param port Port
     */
    public UdpServer(int port) {
        this.port = port;
    }

    @Override
    protected DatagramSocket prepareSocket() throws SocketException {
        LOGGER.info("Preparing UDP server socket on port " + port);
        return new DatagramSocket(port);
    }

    @Override
    protected void onExceptionDuringTick(Throwable throwable) {
        LOGGER.error("Exception occurred during server tick", throwable);
    }
}
