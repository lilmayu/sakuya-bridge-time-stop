package dev.mayuna.sakuyabridge.commons.v2.objects.users;

import lombok.Data;

import java.util.UUID;

/**
 * Represents a user.
 */
@Data
public final class User {

    /**
     * Same as account UUID
     */
    private UUID uuid;
    private UserStatistics statistics;

    /**
     * Used for serialization.
     */
    public User() {
    }

    /**
     * Creates a new user with the given UUID.
     *
     * @param uuid       The UUID
     * @param statistics The statistics
     */
    public User(UUID uuid, UserStatistics statistics) {
        this.uuid = uuid;
        this.statistics = statistics;
    }
}
