package dev.mayuna.sakuyabridge.server.v2.networking.listeners;

import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.timestop.networking.timestop.TimeStopPackets;

/**
 * A helper for creating responses
 *
 * @param <TRequest>  The request
 * @param <TResponse> The response
 */
public interface ResponseHelper<TRequest extends TimeStopPackets.BasePacket, TResponse extends TimeStopPackets.BasePacket> {

    /**
     * Creates a new response
     *
     * @return The response
     */
    TResponse createResponse();

    /**
     * Responds to the specified request with the specified response
     *
     * @param connection The connection
     * @param request    The request
     * @param response   The response
     */
    default void respond(SakuyaBridgeConnection connection, TRequest request, TimeStopPackets.BasePacket response) {
        connection.sendTCP(response.withResponseTo(request));
    }

    /**
     * Responds to the specified request with created response with the specified error
     *
     * @param connection The connection
     * @param request    The request
     * @param error      The error
     */
    default void respondError(SakuyaBridgeConnection connection, TRequest request, String error) {
        TimeStopPackets.BasePacket response = createResponse();
        response.withError(error);
        respond(connection, request, response);
    }

    /**
     * Responds to the specified request with created response with empty response
     *
     * @param connection The connection
     * @param request    The request
     */
    default void respondEmpty(SakuyaBridgeConnection connection, TRequest request) {
        respond(connection, request, createResponse());
    }
}
