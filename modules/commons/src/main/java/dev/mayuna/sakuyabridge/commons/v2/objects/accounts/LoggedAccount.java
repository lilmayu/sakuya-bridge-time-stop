package dev.mayuna.sakuyabridge.commons.v2.objects.accounts;

import java.util.UUID;

/**
 * Represents an account that is logged in.<br>
 * This is mainly when you do not want to expose other information about the account, e.g., password hash, when sending it to the client.
 */
public final class LoggedAccount extends Account {

    /**
     * Used for serialization.
     */
    public LoggedAccount() {
    }

    /**
     * Creates a new account with the given UUID.
     *
     * @param username The username
     * @param uuid     The UUID
     */
    public LoggedAccount(String username, UUID uuid) {
        super(username, uuid);
    }

    /**
     * Creates a logged account from an account.
     *
     * @param account The account
     *
     * @return The logged account
     */
    public static LoggedAccount fromAccount(Account account) {
        return new LoggedAccount(account.getUsername(), account.getUuid());
    }
}
