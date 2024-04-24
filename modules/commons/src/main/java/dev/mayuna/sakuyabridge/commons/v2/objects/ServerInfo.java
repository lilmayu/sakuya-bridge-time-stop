package dev.mayuna.sakuyabridge.commons.v2.objects;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
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
    private boolean registerEnabled = true; // TODO: Možnost vypnout register (logika)

    /**
     * Used for serialization
     */
    public ServerInfo() {
    }
}
