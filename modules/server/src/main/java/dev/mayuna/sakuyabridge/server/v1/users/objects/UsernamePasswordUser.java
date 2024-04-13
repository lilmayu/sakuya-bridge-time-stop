package dev.mayuna.sakuyabridge.server.v1.users.objects;

import dev.mayuna.pumpk1n.api.DataElement;
import lombok.Getter;

public class UsernamePasswordUser extends User implements DataElement {

    private @Getter String username;
    private @Getter String passwordHash;

    /**
     * For serialization
     */
    public UsernamePasswordUser() {
    }

    /**
     * Creates a new {@link UsernamePasswordUser} with the given username and password hash
     *
     * @param username     The username to use
     * @param passwordHash The password hash to use
     * @param id           The ID to use
     */
    public UsernamePasswordUser(String username, String passwordHash, int id) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.id = id;
    }
}
