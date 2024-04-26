package dev.mayuna.sakuyabridge.server.v2.objects.users;

import dev.mayuna.pumpk1n.api.ParentedDataElement;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Wraps a user for storage
 */
@Getter
@Setter
public final class StorageUserWrap extends ParentedDataElement {

    private User user;

    /**
     * User for serialization
     */
    public StorageUserWrap() {
    }

    /**
     * Creates a new storage user wrap
     *
     * @param user The user
     */
    public StorageUserWrap(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return user.toString();
    }
}
