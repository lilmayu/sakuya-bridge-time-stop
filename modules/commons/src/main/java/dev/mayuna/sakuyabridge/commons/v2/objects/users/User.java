package dev.mayuna.sakuyabridge.commons.v2.objects.users;

import com.google.gson.annotations.Expose;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a user.
 */
@Getter
@Setter
public final class User {

    /**
     * The logged account of the user.<br>
     * Disabled the serialization and deserialization for GSON (when saving to disk).
     */
    @Expose(serialize = false, deserialize = false)
    private LoggedAccount loggedAccount;

    /**
     * Same as account UUID
     */
    private UUID uuid;
    private UserStatistics statistics = new UserStatistics();

    /**
     * Used for serialization.
     */
    public User() {
    }

    /**
     * Creates a new user with the given UUID.
     *
     * @param loggedAccount Logged account
     */
    public User(LoggedAccount loggedAccount) {
        this.loggedAccount = loggedAccount;
        this.uuid = loggedAccount.getUuid();
    }

    /**
     * Gets the username of the user from the account.
     *
     * @return The username
     */
    public String getUsername() {
        return loggedAccount.getUsername();
    }

    @Override
    public String toString() {
        return "User{" +
                "loggedAccount=" + loggedAccount +
                ", uuid=" + uuid +
                '}';
    }
}
