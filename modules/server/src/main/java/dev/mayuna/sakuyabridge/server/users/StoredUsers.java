package dev.mayuna.sakuyabridge.server.users;

import dev.mayuna.pumpk1n.api.DataElement;
import dev.mayuna.sakuyabridge.server.users.objects.StorageUser;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class StoredUsers implements DataElement {

    private List<StorageUser> users = new LinkedList<>();

    public StoredUsers() {
    }

    /**
     * Makes the stored users list synchronized
     */
    public void synchronizedList() {
        users = Collections.synchronizedList(users);
    }

    /**
     * Creates a new user with the given username and adds it to the stored users list
     *
     * @param username The username to use
     *
     * @return The created user
     */
    public StorageUser createUser(String username) {
        synchronized (users) {
            StorageUser storageUser = new StorageUser(username, getNextId());
            addOrReplaceUser(storageUser);
            return storageUser;
        }
    }

    /**
     * Gets a user from the stored users list by username
     *
     * @param username The username to get
     *
     * @return The user, or null if not found
     */
    public StorageUser getUser(String username) {
        synchronized (users) {
            return users.stream().filter(storageUser -> storageUser.getUsername().equals(username)).findFirst().orElse(null);
        }
    }

    /**
     * Gets a user from the stored users list by ID
     *
     * @param id The ID to get
     *
     * @return The user, or null if not found
     */
    public StorageUser getUser(int id) {
        synchronized (users) {
            return users.stream().filter(storageUser -> storageUser.getId() == id).findFirst().orElse(null);
        }
    }

    /**
     * Adds or replaces a user in the stored users list. If the user by username already exists, an {@link IllegalArgumentException} will be thrown.
     * If the user doesn't have an ID, the next ID will be used.
     *
     * @param user The user to add or replace
     */
    public void addOrReplaceUser(StorageUser user) {
        if (userWithUsernameExists(user.getUsername())) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists");
        }

        // If the user doesn't have an ID, set it to the next ID
        if (user.getId() == 0) {
            user.setId(getNextId());
        }

        synchronized (users) {
            users.removeIf(storageUser -> storageUser.getId() == user.getId());
            users.add(user);
        }
    }

    /**
     * Removes a user from the stored users list by ID
     *
     * @param id The ID to remove
     */
    public void removeUser(int id) {
        synchronized (users) {
            users.removeIf(storageUser -> storageUser.getId() == id);
        }
    }

    /**
     * Checks if a user with the given username exists
     *
     * @param username The username to check
     *
     * @return True if the user exists, false if not
     */
    public boolean userWithUsernameExists(String username) {
        synchronized (users) {
            return users.stream().anyMatch(storageUser -> storageUser.getUsername().equals(username));
        }
    }

    /**
     * Gets the next ID to use
     *
     * @return The next ID to use
     */
    public int getNextId() {
        synchronized (users) {
            return users.stream().mapToInt(StorageUser::getId).max().orElse(0) + 1;
        }
    }
}
