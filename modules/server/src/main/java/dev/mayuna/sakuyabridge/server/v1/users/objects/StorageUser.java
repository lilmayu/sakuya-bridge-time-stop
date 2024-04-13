package dev.mayuna.sakuyabridge.server.v1.users.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageUser extends User {

    private String username;

    /**
     * For serialization
     */
    public StorageUser() {
    }

    /**
     * Creates a new {@link StorageUser} with the given username and id
     *
     * @param username The username to use
     * @param id       The ID to use
     */
    public StorageUser(String username, int id) {
        this.username = username;
        this.id = id;
    }
}
