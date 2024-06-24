package dev.mayuna.sakuyabridge.commons.v2.objects;

import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Information about the server
 */
@Data
public final class ServerInfo {

    private UUID uuid = UUID.randomUUID();
    private String name = "Unnamed Sakuya Bridge Server";
    private String maintainer = "Unknown";
    private String region = "Za Warudo";
    private String motd = "Welcome to Sakuya Bridge!";
    private List<AuthenticationMethods> authenticationMethods = new LinkedList<>();
    private boolean registerEnabled = true;
    private boolean chatEnabled = true;

    /**
     * Used for serialization
     */
    public ServerInfo() {
    }

    /**
     * Determines if the given authentication method is enabled
     *
     * @param method The method
     *
     * @return True if the method is enabled
     */
    public boolean isAuthenticationMethodEnabled(AuthenticationMethods method) {
        return authenticationMethods.contains(method);
    }
}
