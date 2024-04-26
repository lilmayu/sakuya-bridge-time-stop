package dev.mayuna.sakuyabridge.commons.v2.objects.auth;

import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Represents a session token
 */
@Getter
public final class SessionToken {

    private LoggedAccount loggedAccount;
    private UUID token;
    private @Setter long expirationTimeMillis;

    /**
     * For serialization
     */
    public SessionToken() {
    }

    /**
     * Creates a new session token
     *
     * @param loggedAccount        The logged account
     * @param token                The token
     * @param expirationTimeMillis The expiration time (UTC)
     */
    public SessionToken(LoggedAccount loggedAccount, UUID token, long expirationTimeMillis) {
        this.loggedAccount = loggedAccount;
        this.token = token;
        this.expirationTimeMillis = expirationTimeMillis;
    }

    /**
     * Creates a new session token
     *
     * @param account              The account
     * @param expirationTimeMillis The expiration time (UTC)
     *
     * @return The session token
     */
    public static SessionToken create(Account account, long expirationTimeMillis) {
        return new SessionToken(LoggedAccount.fromAccount(account), UUID.randomUUID(), expirationTimeMillis);
    }

    /**
     * Gets the expiration time in a pretty format
     *
     * @return The expiration time in a pretty format
     */
    public String getExpirationTimePretty() {
        return SimpleDateFormat.getDateTimeInstance().format(expirationTimeMillis);
    }

    /**
     * Checks if the token is expired
     *
     * @return Whether the token is expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTimeMillis;
    }
}
