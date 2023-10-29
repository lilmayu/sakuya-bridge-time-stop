package dev.mayuna.sakuyabridge.server.users.managers;

import dev.mayuna.sakuyabridge.server.Main;
import dev.mayuna.sakuyabridge.server.auth.PasswordAuthentication;
import dev.mayuna.sakuyabridge.server.users.StoredUsers;
import dev.mayuna.sakuyabridge.server.users.objects.StorageUser;
import dev.mayuna.sakuyabridge.server.users.objects.UsernamePasswordUser;

public class UsernamePasswordUserManager {

    private final PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
    private final StoredUsers storedUsers;

    /**
     * Creates a new {@link UsernamePasswordUserManager} with the given {@link StoredUsers}
     *
     * @param storedUsers The {@link StoredUsers} to use
     */
    public UsernamePasswordUserManager(StoredUsers storedUsers) {
        this.storedUsers = storedUsers;
    }

    /**
     * Creates a new {@link UsernamePasswordUser} with the given username and password
     *
     * @param username The username to use
     * @param password The password to use
     *
     * @return The created {@link UsernamePasswordUser}
     */
    public UsernamePasswordUser createUser(String username, String password) {
        synchronized (storedUsers.getUsers()) {
            StorageUser storageUser = storedUsers.createUser(username); // Throws if user already exists
            String passwordHash = passwordAuthentication.hash(password.toCharArray());

            UsernamePasswordUser user = new UsernamePasswordUser(username, passwordHash, storageUser.getId());
            Main.getPumpk1n().getOrCreateDataHolder(user.getUserUUID()).addOrReplaceDataElement(user);
            return user;
        }
    }

    /**
     * Authenticates the given username and password
     *
     * @param username The username to authenticate
     * @param password The password to authenticate
     *
     * @return Whether the authentication was successful
     */
    public boolean authenticateUser(String username, String password) {
        synchronized (storedUsers.getUsers()) {
            StorageUser storageUser = storedUsers.getUser(username);

            if (storageUser == null) {
                throw new IllegalArgumentException("User with " + username + " does not exist");
            }

            UsernamePasswordUser user = Main.getPumpk1n().getOrCreateDataHolder(storageUser.getUserUUID()).getDataElement(UsernamePasswordUser.class);
            return passwordAuthentication.authenticate(password, user.getPasswordHash());
        }
    }
}
