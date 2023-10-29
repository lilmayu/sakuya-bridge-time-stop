package dev.mayuna.sakuyabridge.server.objects.users;

import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class User {

    private int id;

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
}
