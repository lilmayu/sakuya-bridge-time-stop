package dev.mayuna.sakuyabridge.server.v2.managers.user;

import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import lombok.NonNull;

import java.util.*;

/**
 * A manager for users
 *
 * @param <T> The type of the user
 */
public abstract class UserManager<T extends User> {

    private final List<T> users = Collections.synchronizedList(new LinkedList<>());

    /**
     * Gets all users
     *
     * @return Unmodifiable list of users
     */
    public List<T> getUsers() {
        return Collections.unmodifiableList(users);
    }

    /**
     * Gets a user by their UUID
     *
     * @param uuid The UUID
     *
     * @return Optional of the user
     */
    public Optional<T> getUser(@NonNull UUID uuid) {
        synchronized (users) {
            return users.stream().filter(user -> user.getUuid().equals(uuid)).findFirst();
        }
    }

    /**
     * Gets a user by their username
     *
     * @param username The username
     *
     * @return Optional of the user
     */
    public Optional<T> getUser(@NonNull String username) {
        synchronized (users) {
            return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
        }
    }

    /**
     * Adds a user
     *
     * @param user The user
     */
    public boolean addUser(T user) {
        synchronized (users) {
            return users.add(user);
        }
    }

    /**
     * Removes a user
     *
     * @param user The user
     */
    public boolean removeUser(T user) {
        synchronized (users) {
            return users.remove(user);
        }
    }

    /**
     * Removes a user by their UUID
     *
     * @param uuid The UUID
     */
    public boolean removeUser(UUID uuid) {
        synchronized (users) {
            return users.removeIf(user -> user.getUuid().equals(uuid));
        }
    }

    /**
     * Removes a user by their username
     *
     * @param username The username
     */
    public boolean removeUser(String username) {
        synchronized (users) {
            return users.removeIf(user -> user.getUsername().equals(username));
        }
    }
}
