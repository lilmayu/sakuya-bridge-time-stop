package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import lombok.Getter;
import lombok.Setter;

import java.net.DatagramPacket;

/**
 * Represents an external UDP client with last datagram packet received at millis
 */
@Getter
@Setter
public final class UdpExternalClient {

    private final UdpDestination destination;
    private long lastDatagramPacketAtMillis = System.currentTimeMillis();

    public UdpExternalClient(UdpDestination destination) {
        this.destination = destination;
    }

    /**
     * Updates the last datagram packet received at millis to the current time.
     */
    public void updateLastDatagramPacketAtMillis() {
        lastDatagramPacketAtMillis = System.currentTimeMillis();
    }

    /**
     * Checks if the client is inactive
     *
     * @param inactiveAfterMillis Inactive after millis
     *
     * @return True if the client is inactive
     */
    public boolean isInactive(long inactiveAfterMillis) {
        return System.currentTimeMillis() - lastDatagramPacketAtMillis > inactiveAfterMillis;
    }

    /**
     * Creates a DatagramPacket with the given data to the destination ({@link UdpDestination#createDatagramPacket(byte[])}
     *
     * @param data Data
     *
     * @return DatagramPacket
     */
    public DatagramPacket createDatagramPacket(byte[] data) {
        return destination.createDatagramPacket(data);
    }

    /**
     * Checks if the given DatagramPacket is from this client ({@link UdpDestination#isFrom(DatagramPacket)})
     *
     * @param datagramPacket DatagramPacket
     *
     * @return True if the DatagramPacket is from this client
     */
    public boolean isFrom(DatagramPacket datagramPacket) {
        return destination.isFrom(datagramPacket);
    }

    @Override
    public String toString() {
        return destination.toString();
    }
}
