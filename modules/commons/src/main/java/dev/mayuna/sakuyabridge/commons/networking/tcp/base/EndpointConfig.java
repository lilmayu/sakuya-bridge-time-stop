package dev.mayuna.sakuyabridge.commons.networking.tcp.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration for endpoints (client and server)
 */
@Getter @Setter
public class EndpointConfig {

    private int maxThreads = 1;

}
