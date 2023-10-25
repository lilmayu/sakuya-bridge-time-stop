package dev.mayuna.sakuyabridge.client.networking;

import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;

import java.util.concurrent.CompletableFuture;

public class NetworkTask {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(NetworkTask.class);

    /**
     * Sends a packet to the server and returns the response
     *
     * @param a        Packet
     * @param response Response class
     * @param <A>      Packet type
     * @param <R>      Response type
     *
     * @return CompletableFuture of the response
     */
    private static <A, R> CompletableFuture<R> sendPacket(A a, Class<R> response) {
        var future = new CompletableFuture<R>();

        Main.getClient().sendTCPWithResponse(
                a,
                response,
                Main.getConfigs().getServerConnectConfig().getTimeoutMillis(),
                future::complete,
                () -> {
                    LOGGER.error("Request with data '" + a.getClass().getName() + "' and response '" + response.getName() + "' timed out");
                    future.completeExceptionally(new RuntimeException("Request timed out"));
                }
        );

        return future;
    }

    /**
     * Exchanges the protocol version with the server
     */
    public static class ExchangeProtocolVersion extends BaseNetworkTask<Integer, Packets.ProtocolVersionExchange> {

        /**
         * Exchanges the protocol version with the server
         *
         * @param protocolVersion Protocol version
         *
         * @return CompletableFuture of {@link Packets.ProtocolVersionExchange}
         */
        @Override
        public CompletableFuture<Packets.ProtocolVersionExchange> run(Integer protocolVersion) {
            return sendPacket(Packets.ProtocolVersionExchange.create(protocolVersion), Packets.ProtocolVersionExchange.class);
        }
    }

    /**
     * Exchanges the asymmetric key with the server and returns the encrypted symmetric key
     */
    public static class ExchangeAsymmetricKey extends BaseNetworkTask<byte[], Packets.SymmetricKeyExchange> {

        /**
         * Exchanges the asymmetric key with the server and returns the encrypted symmetric key
         *
         * @param publicKey Public key
         *
         * @return CompletableFuture of {@link Packets.AsymmetricKeyExchange}
         */
        @Override
        public CompletableFuture<Packets.SymmetricKeyExchange> run(byte[] publicKey) {
            return sendPacket(Packets.AsymmetricKeyExchange.create(publicKey), Packets.SymmetricKeyExchange.class);
        }
    }
}
