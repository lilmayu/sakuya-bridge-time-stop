package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import lombok.Getter;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * UDP server bridge<br>
 * Bridges UDP packets between the host and clients<br>
 * Also manages inactive clients
 */
public final class UdpServerBridge extends UdpServer {

    private final long inactiveAfterMillis;
    private final List<UdpExternalClient> clients = new LinkedList<>();
    private final Timer inactiveClientTimer = new Timer();

    private final @Getter UdpDestinationWhitelist clientWhitelist = new UdpDestinationWhitelist();
    private final @Getter UdpDestinationWhitelist hostWhitelist = new UdpDestinationWhitelist();

    // TODO: Whitelisting clients, somehow

    private UdpExternalClient host;

    /**
     * Creates a new UDP server
     *
     * @param port Port
     */
    public UdpServerBridge(int port, long inactiveAfterMillis) {
        super(port);
        this.inactiveAfterMillis = inactiveAfterMillis;

        prepareInactiveClientTimer();
    }

    /**
     * Prepares the inactive client timer
     */
    private void prepareInactiveClientTimer() {
        inactiveClientTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (clients) {
                    clients.removeIf(client -> {
                        if (client.isInactive(inactiveAfterMillis)) {
                            LOGGER.info("Client disconnected (inactive): {}", client);
                            return true;
                        }

                        return false;
                    });
                }
            }
        }, 0, inactiveAfterMillis);
    }

    @Override
    protected void onReceiveDatagramPacket(DatagramPacket receivedDatagram) {
        // First packet is always from the host, dummy packet
        if (host == null) {
            host = new UdpExternalClient(UdpDestination.fromDatagramPacket(receivedDatagram));
            LOGGER.info("Host connected: {}", host);
            return;
        }

        // Check if received packet is from new client
        synchronized (clients) {
            if (clients.stream().noneMatch(client -> client.isFrom(receivedDatagram))) {
                UdpExternalClient client = new UdpExternalClient(UdpDestination.fromDatagramPacket(receivedDatagram));
                clients.add(client);
                LOGGER.info("Client connected: {}", client);
            }
        }

        // Dummy packet, ignore
        if (receivedDatagram.getLength() == 0) {
            return;
        }

        // If the packet is from host, bridge it to all clients
        if (host.isFrom(receivedDatagram)) {
            host.updateLastDatagramPacketAtMillis();

            synchronized (clients) {
                var iterator = clients.iterator();

                while (iterator.hasNext()) {
                    UdpExternalClient client = iterator.next();
                    DatagramPacket datagram = client.createDatagramPacket(receivedDatagram.getData());

                    try {
                        sendDatagramPacket(datagram);
                    } catch (Exception exception) {
                        LOGGER.warn("Client disconnected (exception occurred): " + client, exception);
                        iterator.remove();
                    }
                }
            }

            return;
        }

        // If the packet is from client, bridge it to the host
        DatagramPacket datagram = host.createDatagramPacket(receivedDatagram.getData());

        try {
            sendDatagramPacket(datagram);
        } catch (Exception exception) {
            LOGGER.warn("Host disconnected (exception occurred): " + host + " - Stopping...", exception);
            stop();
            return;
        }

        // Update last datagram packet received at millis for the client that sent the packet
        synchronized (clients) {
            clients.stream()
                   .filter(client -> client.isFrom(receivedDatagram))
                   .forEach(UdpExternalClient::updateLastDatagramPacketAtMillis);
        }
    }

    @Override
    protected void onExceptionDuringTick(Throwable throwable) {
        // TODO: Remove client that caused the exception during tick
        // TODO: If it's host -> stop the server
        super.onExceptionDuringTick(throwable);
    }

    /**
     * Stops the server's inactive client timer, clears clients, sets host as null and stops the server itself
     */
    @Override
    public void stop() {
        inactiveClientTimer.cancel();

        synchronized (clients) {
            clients.clear();
        }

        host = null;

        super.stop();
    }
}
