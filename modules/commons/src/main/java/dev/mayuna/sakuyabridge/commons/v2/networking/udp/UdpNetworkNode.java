package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import lombok.Getter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * UDP network node<br>
 * Holds the basic methods for a UDP network node
 */
@Getter
public abstract class UdpNetworkNode {

    public static final int DEFAULT_PACKET_BUFFER_SIZE = 128;

    protected DatagramSocket datagramSocket;
    protected Thread tickThread;

    // External listeners
    private Consumer<DatagramPacket> onDatagramPacketReceived;
    private Consumer<Throwable> onExceptionDuringReceive;
    private BiConsumer<DatagramPacket, Throwable> onExceptionDuringReceiveProcess;
    private Consumer<Throwable> onExceptionDuringSend;
    private Runnable onStarted;
    private Runnable onStopped;

    public UdpNetworkNode() {
        // Prepare the listeners
        setOnDatagramPacketReceived(null);
        setOnExceptionDuringReceive(null);
        setOnExceptionDuringReceiveProcess(null);
        setOnExceptionDuringSend(null);
        setOnStarted(null);
        setOnStopped(null);
    }

    /**
     * Prepares the socket for the network node.
     *
     * @throws SocketException If an error occurs while preparing the socket.
     */
    protected abstract DatagramSocket prepareSocket() throws SocketException;

    /**
     * Starts the network node.
     *
     * @throws SocketException If an error occurs while preparing the socket.
     */
    public void start() throws SocketException {
        if (isRunning()) {
            return;
        }

        datagramSocket = prepareSocket();

        tickThread = new Thread(() -> {
            while (isRunning()) {
                tick();
            }
        });

        tickThread.setName("UDP-NODE-TICKER");
        tickThread.start();

        onStarted.run();
    }

    /**
     * Stops the network node.
     */
    public void stop() {
        if (isRunning()) {
            getDatagramSocket().close();
        }

        if (tickThread != null) {
            tickThread.interrupt();
        }

        onStopped.run();
    }

    /**
     * Checks if the network node is running (e.g. the socket is connected).
     *
     * @return If the network node is running.
     */
    public boolean isRunning() {
        var datagramSocket = getDatagramSocket();

        return datagramSocket != null && datagramSocket.isBound();
    }

    /**
     * Ticks the network node (e.g. checks for incoming packets, receives it, handles it, etc.)
     */
    protected void tick() {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[DEFAULT_PACKET_BUFFER_SIZE], DEFAULT_PACKET_BUFFER_SIZE);

        try {
            getDatagramSocket().receive(datagramPacket);
        } catch (IOException exception) {
            if (!isRunning()) {
                // Socket is closed, no need to handle the exception, just return
                return;
            }

            onExceptionDuringReceive(exception);
            onExceptionDuringReceive.accept(exception);
        }

        // Cut the excess bytes from the buffer
        try {
            byte[] data = new byte[datagramPacket.getLength()];
            System.arraycopy(datagramPacket.getData(), 0, data, 0, datagramPacket.getLength());
            datagramPacket.setData(data);

            onReceiveDatagramPacket(datagramPacket);
            onDatagramPacketReceived.accept(datagramPacket);
        } catch (Exception exception) {
            onExceptionDuringReceiveProcess(datagramPacket, exception);
            onExceptionDuringReceiveProcess.accept(datagramPacket, exception);
        }
    }

    /**
     * Sends a datagram packet.
     *
     * @param datagramPacket The datagram packet to send
     *
     * @throws IOException If an error occurs while sending the datagram packet
     */
    protected void sendDatagramPacket(DatagramPacket datagramPacket) throws IOException {
        try {
            getDatagramSocket().send(datagramPacket);
        } catch (IOException throwable) {
            if (!isRunning()) {
                // Socket is closed, no need to handle the exception, just return
                return;
            }

            throw throwable;
        }
    }

    /**
     * Invoked when a datagram packet is received.
     *
     * @param datagramPacket The datagram packet received
     */
    protected abstract void onReceiveDatagramPacket(DatagramPacket datagramPacket);

    /**
     * Invoked when an exception occurs during the tick.
     *
     * @param throwable The exception that occurred
     */
    protected abstract void onExceptionDuringReceive(Throwable throwable);

    /**
     * Invoked when an exception occurs during the receive process.
     *
     * @param throwable The exception that occurred
     */
    protected abstract void onExceptionDuringReceiveProcess(DatagramPacket datagramPacket, Throwable throwable);

    /**
     * Sets the on datagram packet received listener.
     *
     * @param onDatagramPacketReceived On datagram packet received listener (null to clear)
     */
    public void setOnDatagramPacketReceived(Consumer<DatagramPacket> onDatagramPacketReceived) {
        // Clear the listener if null
        this.onDatagramPacketReceived = Objects.requireNonNullElse(onDatagramPacketReceived, datagramPacket -> {
        });
    }

    /**
     * Sets the on exception during tick listener.
     *
     * @param onExceptionDuringReceive On exception during tick listener (null to clear)
     */
    public void setOnExceptionDuringReceive(Consumer<Throwable> onExceptionDuringReceive) {
        // Clear the listener if null
        this.onExceptionDuringReceive = Objects.requireNonNullElse(onExceptionDuringReceive, throwable -> {
        });
    }

    /**
     * Sets the on exception during receive process listener.
     *
     * @param onExceptionDuringReceiveProcess On exception during receive process listener (null to clear)
     */
    public void setOnExceptionDuringReceiveProcess(BiConsumer<DatagramPacket, Throwable> onExceptionDuringReceiveProcess) {
        // Clear the listener if null
        this.onExceptionDuringReceiveProcess = Objects.requireNonNullElse(onExceptionDuringReceiveProcess, (datagramPacket, throwable) -> {
        });
    }

    /**
     * Sets the on exception during send listener.
     *
     * @param onExceptionDuringSend On exception during send listener (null to clear)
     */
    public void setOnExceptionDuringSend(Consumer<Throwable> onExceptionDuringSend) {
        // Clear the listener if null
        this.onExceptionDuringSend = Objects.requireNonNullElse(onExceptionDuringSend, throwable -> {
        });
    }

    /**
     * Sets the on stopped listener (e.g. when the network node is stopped).
     *
     * @param onStopped On datagram packet received listener (null to clear)
     */
    public void setOnStopped(Runnable onStopped) {
        // Clear the listener if null
        this.onStopped = Objects.requireNonNullElse(onStopped, () -> {
        });
    }

    /**
     * Sets the on started listener.
     *
     * @param onStarted On started listener (null to clear)
     */
    public void setOnStarted(Runnable onStarted) {
        // Clear the listener if null
        this.onStarted = Objects.requireNonNullElse(onStarted, () -> {
        });
    }
}
