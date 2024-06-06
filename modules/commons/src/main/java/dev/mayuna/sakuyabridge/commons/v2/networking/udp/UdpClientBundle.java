package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import lombok.Getter;

import java.io.IOException;
import java.net.SocketException;

/**
 * UDP client bundle which exchanges packets between two clients
 */
@Getter
public final class UdpClientBundle {

    private final UdpClient remoteClient;
    private final UdpClient localClient;

    private long exchangedDatagramPackets = 0;
    private long exchangedBytes = 0;

    /**
     * Creates a new UDP client bundle
     *
     * @param remoteDestination Remote destination
     * @param localDestination  Local destination
     */
    public UdpClientBundle(UdpDestination remoteDestination, UdpDestination localDestination) {
        this.remoteClient = new UdpClient(remoteDestination);
        this.localClient = new UdpClient(localDestination);

        prepareClients();
    }

    /**
     * Prepares the clients
     */
    private void prepareClients() {
        // Exchange between two clients
        remoteClient.setOnDatagramPacketReceived(packet -> {
            exchangedDatagramPackets++;
            exchangedBytes += packet.getLength();

            try {
                localClient.send(packet.getData());
            } catch (IOException e) {
                localClient.getLogger().error("Error while sending packet to local client", e);
                // TODO: What to do with this exception? Stop the clients? It could spam the logs, if unstopped.
                // Maybe add error threshold?
            }
        });

        localClient.setOnDatagramPacketReceived(packet -> {
            exchangedDatagramPackets++;
            exchangedBytes += packet.getLength();

            try {
                remoteClient.send(packet.getData());
            } catch (IOException e) {
                remoteClient.getLogger().error("Error while sending packet to remote client", e);
                // TODO: What to do with this exception? Stop the clients? It could spam the logs, if unstopped.
                // Maybe add error threshold?
            }
        });
    }

    /**
     * Sets the remote destination<br>
     * Used when UDP hole punching
     *
     * @param udpDestination The remote destination
     */
    public void setRemoteDestination(UdpDestination udpDestination) {
        remoteClient.setUdpDestination(udpDestination);
    }

    /**
     * Starts the clients
     *
     * @throws SocketException If an error occurs while preparing the socket
     */
    public void startClients() throws SocketException {
        localClient.start();
        remoteClient.start();
    }

    /**
     * Stops the clients
     */
    public void stopClients() {
        localClient.stop();
        remoteClient.stop();
    }
}
