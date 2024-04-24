package dev.mayuna.sakuyabridge.commons.v2.objects.accounts;

import lombok.Getter;

import java.util.UUID;

/**
 * Represents an account.
 */
@Getter
public abstract class Account {

    private String username;
    private UUID uuid;

    /**
     * Used for serialization.
     */
    public Account() {
    }

    /**
     * Creates a new account with the given username and a random UUID.
     *
     * @param username The username
     */
    public Account(String username) {
        this.username = username;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Creates a new account with the given UUID.
     *
     * @param username The username
     * @param uuid     The UUID
     */
    public Account(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
