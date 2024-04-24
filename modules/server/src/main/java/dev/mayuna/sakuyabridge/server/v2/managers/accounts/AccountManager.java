package dev.mayuna.sakuyabridge.server.v2.managers.accounts;

import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import lombok.NonNull;

import java.util.*;

/**
 * A manager for accounts
 *
 * @param <T> The type of the account
 */
public abstract class AccountManager<T extends Account> {

    private final List<T> loadedAccounts = Collections.synchronizedList(new LinkedList<>());

    /**
     * Prepares the account manager
     */
    public abstract void prepare();

    /**
     * Shuts down the account manager
     */
    protected void shutdown() {
        saveAllAccounts(loadedAccounts);
    }

    /**
     * Creates an account with the given username, regardless if the username is taken or not<br>
     * Does not add it to the database
     *
     * @param username The username
     *
     * @return The account
     */
    protected abstract @NonNull T createAccount(@NonNull String username);

    /**
     * Deletes an account from the database
     *
     * @param uuid The UUID of the account
     *
     * @return Whether the account was deleted
     */
    public abstract boolean deleteAccount(@NonNull UUID uuid);

    /**
     * Saves an account to the database
     *
     * @param account The account
     */
    protected abstract void saveAccount(@NonNull T account);

    /**
     * Loads an account from the database
     *
     * @param uuid The UUID of the account
     *
     * @return The account
     */
    protected abstract Optional<T> loadAccount(@NonNull UUID uuid);

    /**
     * Loads an account from the database
     *
     * @param username The username of the account
     *
     * @return The account
     */
    protected abstract Optional<T> loadAccount(@NonNull String username);

    /**
     * Checks if an account exists in the database
     *
     * @param uuid The UUID of the account
     *
     * @return Whether the account exists
     */
    public abstract boolean accountExistsInStorage(@NonNull UUID uuid);

    /**
     * Checks if an account exists in the database
     *
     * @param username The username of the account
     *
     * @return Whether the account exists
     */
    public abstract boolean accountExistsInStorage(@NonNull String username);

    /**
     * Tries to create an account with the given username<br>
     * If the username is already taken, it will return an empty optional<br>
     * This will also check if the account exists in the database
     *
     * @param username The username
     *
     * @return Optional of the account
     */
    protected Optional<T> tryCreateAccount(@NonNull String username) {
        synchronized (loadedAccounts) {
            if (loadedAccounts.stream().anyMatch(account -> account.getUsername().equals(username))) {
                return Optional.empty();
            }

            if (accountExistsInStorage(username)) {
                return Optional.empty();
            }

            T account = createAccount(username);
            addAccount(account);
            saveAccount(account);
            return Optional.of(account);
        }
    }

    /**
     * Saves all accounts to the database
     *
     * @param accounts The accounts
     */
    protected void saveAllAccounts(@NonNull List<T> accounts) {
        accounts.forEach(this::saveAccount);
    }

    /**
     * Gets all loaded accounts
     *
     * @return Unmodifiable list of accounts
     */
    public List<T> getLoadedAccounts() {
        return Collections.unmodifiableList(loadedAccounts);
    }

    /**
     * Gets an account by their UUID
     *
     * @param uuid The UUID
     *
     * @return Optional of the account
     */
    public Optional<T> getAccount(@NonNull UUID uuid) {
        synchronized (loadedAccounts) {
            return loadedAccounts.stream().filter(account -> account.getUuid().equals(uuid)).findFirst();
        }
    }

    /**
     * Gets an account by their username
     *
     * @param username The username
     *
     * @return Optional of the account
     */
    public Optional<T> getAccount(@NonNull String username) {
        synchronized (loadedAccounts) {
            return loadedAccounts.stream().filter(account -> account.getUsername().equals(username)).findFirst();
        }
    }

    /**
     * Gets an account by their UUID, or loads it if it isn't loaded in memory
     *
     * @param uuid The UUID
     *
     * @return Optional of the account
     */
    public Optional<T> getOrLoadAccount(@NonNull UUID uuid) {
        synchronized (loadedAccounts) {
            var account = getAccount(uuid);

            if (account.isPresent()) {
                return account;
            }

            return loadAccount(uuid);
        }
    }

    /**
     * Gets an account by their username, or loads it if it isn't loaded in memory
     *
     * @param username The username
     *
     * @return Optional of the account
     */
    public Optional<T> getOrLoadAccount(@NonNull String username) {
        synchronized (loadedAccounts) {
            var account = getAccount(username);

            if (account.isPresent()) {
                return account;
            }

            return loadAccount(username);
        }
    }

    /**
     * Adds an account
     *
     * @param account The account
     *
     * @return Whether the account was added
     */
    public boolean addAccount(@NonNull T account) {
        synchronized (loadedAccounts) {
            return loadedAccounts.add(account);
        }
    }

    /**
     * Removes an account
     *
     * @param account The account
     *
     * @return Whether the account was removed
     */
    public boolean removeAccount(@NonNull T account) {
        synchronized (loadedAccounts) {
            return loadedAccounts.remove(account);
        }
    }

    /**
     * Removes an account by their UUID
     *
     * @param uuid The UUID
     *
     * @return Whether the account was removed
     */
    public boolean removeAccount(@NonNull UUID uuid) {
        synchronized (loadedAccounts) {
            return loadedAccounts.removeIf(account -> account.getUuid().equals(uuid));
        }
    }

    /**
     * Removes an account by their username
     *
     * @param username The username
     *
     * @return Whether the account was removed
     */
    public boolean removeAccount(@NonNull String username) {
        synchronized (loadedAccounts) {
            return loadedAccounts.removeIf(account -> account.getUsername().equals(username));
        }
    }

    /**
     * Deletes an account from the database
     *
     * @param account The account
     *
     * @return Whether the account was deleted
     */
    public final boolean deleteAccount(@NonNull T account) {
        return deleteAccount(account.getUuid());
    }
}
