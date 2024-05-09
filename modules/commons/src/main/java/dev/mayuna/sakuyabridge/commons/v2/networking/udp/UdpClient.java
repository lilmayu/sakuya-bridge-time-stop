package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * UDP client
 */
@Getter
@Setter
public final class UdpClient extends UdpNetworkNode {

    /**
     * There can be more clients running at the same time
     */
    private static int clientId = 0;

    private final SakuyaBridgeLogger logger = SakuyaBridgeLogger.create("UdpClient-" + clientId++);

    private UdpDestination udpDestination;

    /**
     * Creates a new UDP client
     *
     * @param udpDestination Destination
     */
    public UdpClient(UdpDestination udpDestination) {
        this.udpDestination = udpDestination;
    }

    @Override
    protected DatagramSocket prepareSocket() throws SocketException {
        logger.info("Preparing UDP client socket for destination {}", udpDestination);
        return new DatagramSocket();
    }

    /**
     * Sends a packet to the destination
     *
     * @param data Datagram data
     *
     * @throws IOException If an error occurs while sending the packet
     */
    public void send(byte[] data) throws IOException {
        super.sendDatagramPacket(udpDestination.createDatagramPacket(data));
    }

    /**
     * Sends a dummy packet to the destination
     *
     * @throws IOException If an error occurs while sending the packet
     */
    public void sendDummy() throws IOException {
        super.sendDatagramPacket(udpDestination.createDatagramPacket(new byte[0]));
    }

    @Override
    protected void onReceiveDatagramPacket(DatagramPacket datagramPacket) {

    }

    @Override
    protected void onExceptionDuringTick(Throwable throwable) {
        logger.error("Exception occurred during client tick", throwable);
    }
}
