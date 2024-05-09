package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import lombok.Getter;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Represents a UDP destination (IP address and port)
 */
@Getter
public final class UdpDestination {

    private InetAddress destinationAddress;
    private int port;

    /**
     * Creates a new UDP destination
     *
     * @param destinationAddress Destination address
     * @param port               Port
     */
    private UdpDestination(InetAddress destinationAddress, int port) {
        this.destinationAddress = destinationAddress;
        this.port = port;
    }

    /**
     * Creates a new UDP destination
     *
     * @param destinationAddress Destination address
     * @param port               Port
     *
     * @return UDP destination
     */
    public static UdpDestination of(InetAddress destinationAddress, int port) {
        return new UdpDestination(destinationAddress, port);
    }

    /**
     * Creates a new UDP destination from a DatagramPacket
     *
     * @param datagramPacket DatagramPacket
     *
     * @return UDP destination
     */
    public static UdpDestination fromDatagramPacket(DatagramPacket datagramPacket) {
        return new UdpDestination(datagramPacket.getAddress(), datagramPacket.getPort());
    }

    /**
     * Sets the destination address and port
     *
     * @param destinationAddress Destination address
     * @param port               Port
     */
    public void setDestination(InetAddress destinationAddress, int port) {
        this.destinationAddress = destinationAddress;
        this.port = port;
    }

    /**
     * Sets the destination address and port from another UdpDestination
     *
     * @param destination Destination
     */
    public void setDestinationFrom(UdpDestination destination) {
        this.destinationAddress = destination.getDestinationAddress();
        this.port = destination.getPort();
    }

    /**
     * Creates a DatagramPacket with the given data to the destination
     *
     * @param data Data
     *
     * @return DatagramPacket
     */
    public DatagramPacket createDatagramPacket(byte[] data) {
        return new DatagramPacket(data, data.length, destinationAddress, port);
    }

    /**
     * Checks if the DatagramPacket is from this destination
     *
     * @param datagramPacket DatagramPacket
     *
     * @return If the DatagramPacket is from this destination
     */
    public boolean isFrom(DatagramPacket datagramPacket) {
        return destinationAddress.equals(datagramPacket.getAddress()) && port == datagramPacket.getPort();
    }

    @Override
    public String toString() {
        return destinationAddress + ":" + port;
    }
}
