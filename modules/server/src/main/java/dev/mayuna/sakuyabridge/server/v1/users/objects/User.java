package dev.mayuna.sakuyabridge.server.v1.users.objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter @Setter
public abstract class User {

    protected int id;

    /**
     * Returns the id of this user as a UUID
     *
     * @param username The username to use
     * @param id       The id to use
     *
     * @return UUID
     */
    public static UUID getUserUUID(String username, int id) {
        return UUID.nameUUIDFromBytes((username + "#" + id).getBytes());
    }

    /**
     * Returns the username of this user
     *
     * @return Username
     */
    abstract @NonNull String getUsername();

    /**
     * Returns the fully qualified name of this user, which is the username followed by a hash and the id
     *
     * @return Fully qualified name
     */
    public String getFullyQualifiedName() {
        return getUsername() + "#" + getId();
    }

    /**
     * Returns the id of this user as a UUID
     *
     * @return UUID
     */
    public UUID getUserUUID() {
        return UUID.nameUUIDFromBytes(getFullyQualifiedName().getBytes());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User user)) {
            return false;
        }
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
