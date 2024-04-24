package dev.mayuna.sakuyabridge.client.v2.backend.enums;

import lombok.Getter;

@Getter
public enum ExchangeVersionResult {
    TIMED_OUT,
    SUPPORTED,
    UNSUPPORTED,
    INVALID;

    private int serverVersion;
    private int networkProtocol;

    /**
     * Sets the server version
     *
     * @param serverVersion The server version
     *
     * @return The result
     */
    public ExchangeVersionResult withServerVersion(int serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    /**
     * Sets the network protocol
     *
     * @param networkProtocol The network protocol
     *
     * @return The result
     */
    public ExchangeVersionResult withNetworkProtocol(int networkProtocol) {
        this.networkProtocol = networkProtocol;
        return this;
    }
}
