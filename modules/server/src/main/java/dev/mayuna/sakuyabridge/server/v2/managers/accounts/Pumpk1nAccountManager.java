package dev.mayuna.sakuyabridge.server.v2.managers.accounts;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.api.ParentedDataElement;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// TODO: UNIT TESTY!!!

/**
 * A manager for accounts (data storage managed by Pumpk1n)
 *
 * @param <T> The type of the account
 */
public abstract class Pumpk1nAccountManager<T extends Account> extends AccountManager<T> {

    /**
     * The UUID of the main data holder, which holds list of all accounts
     */
    public static final UUID MAIN_DATAHOLDER_UUID = UUID.fromString("02fb64d1-6fa0-4c40-a7b2-cab12c1b6c06");

    private final SakuyaBridgeLogger logger;
    private final Pumpk1n pumpk1n;

    private AccountListDataElement accountListDataElement = new AccountListDataElement();

    /**
     * Creates a new account manager
     *
     * @param pumpk1n The pumpk1n instance
     */
    public Pumpk1nAccountManager(SakuyaBridgeLogger logger, Pumpk1n pumpk1n) {
        this.logger = logger;
        this.pumpk1n = pumpk1n;
    }

    /**
     * Prepares the account manager
     */
    @Override
    public void prepare() {
        // Load the account list data element
        var dataHolder = pumpk1n.getOrCreateDataHolder(MAIN_DATAHOLDER_UUID);
        accountListDataElement = dataHolder.getOrCreateDataElement(AccountListDataElement.class);

        logger.log(AccountManagerBundle.LOG_LEVEL, "Loaded account list data element (" + accountListDataElement.countAccounts() + " accounts)");
    }

    /**
     * Shuts down the account manager
     */
    @Override
    public void shutdown() {
        super.shutdown();

        // Save the account list data element
        accountListDataElement.save();

        logger.log(AccountManagerBundle.LOG_LEVEL, "Saved account list data element (" + accountListDataElement.countAccounts() + " accounts)");
    }

    /**
     * Deletes an account from the database
     *
     * @param uuid The UUID of the account
     *
     * @return Whether the account was deleted
     */
    @Override
    public boolean deleteAccount(@NonNull UUID uuid) {
        // Remove the account from the list
        boolean deletedSomething = false;

        if (accountListDataElement.removeAccount(uuid)) {
            accountListDataElement.save();
            deletedSomething = true;
        }

        // Remove the account data holder
        var dataHolder = pumpk1n.getOrLoadDataHolder(uuid);
        if (dataHolder != null) {
            dataHolder.delete();
            deletedSomething = true;
        }

        // Log
        if (deletedSomething) {
            logger.log(AccountManagerBundle.LOG_LEVEL, "Deleted account with UUID " + uuid);
        }

        return deletedSomething;
    }

    /**
     * Adds an account to the database
     *
     * @param account The account to add
     *
     * @return True if the account was added, false otherwise
     */
    @Override
    public boolean addAccount(@NonNull T account) {
        boolean added = super.addAccount(account);

        if (added) {
            accountListDataElement.addAccount(account.getUuid(), account.getUsername());
            accountListDataElement.save();
        }

        return added;
    }

    /**
     * Saves an account to the database
     *
     * @param account The account to save
     */
    @Override
    public void saveAccount(@NonNull T account) {
        var dataHolder = pumpk1n.getOrCreateDataHolder(account.getUuid());
        dataHolder.addOrReplaceDataElement(new AccountDataElement<>(account));
        dataHolder.save();

        logger.log(AccountManagerBundle.LOG_LEVEL, "Saved account " + account);
    }

    /**
     * Loads an account from the database
     *
     * @param uuid The UUID of the account
     *
     * @return The account, if it exists
     */
    @Override
    protected Optional<T> loadAccount(@NonNull UUID uuid) {
        // Check if the account exists
        if (!accountListDataElement.containsAccount(uuid)) {
            return Optional.empty();
        }

        // Load the account
        var dataHolder = pumpk1n.getOrLoadDataHolder(uuid);

        if (dataHolder == null) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        var accountDataElement = (AccountDataElement<T>) dataHolder.getDataElement(AccountDataElement.class);

        // Check if the account data element exists
        if (accountDataElement == null) {
            return Optional.empty();
        }

        // Add the account to the storage
        T account = accountDataElement.account;

        if (account == null) {
            // Should never happen
            return Optional.empty();
        }

        logger.log(AccountManagerBundle.LOG_LEVEL, "Loaded account " + account);

        // Return the account
        return Optional.of(accountDataElement.account);
    }

    /**
     * Loads an account from the database
     *
     * @param username The username of the account
     *
     * @return The account, if it exists
     */
    @Override
    protected Optional<T> loadAccount(@NonNull String username) {
        if (!accountListDataElement.containsAccount(username)) {
            return Optional.empty();
        }

        var uuid = accountListDataElement.getAccountUUID(username);

        if (uuid == null) {
            // Should never happen
            return Optional.empty();
        }

        return loadAccount(uuid);
    }

    /**
     * Checks if an account exists in the database
     *
     * @param uuid The UUID of the account
     *
     * @return Whether the account exists
     */
    @Override
    public boolean accountExistsInStorage(@NonNull UUID uuid) {
        return accountListDataElement.containsAccount(uuid);
    }

    /**
     * Checks if an account exists in the database
     *
     * @param username The username of the account
     *
     * @return Whether the account exists
     */
    @Override
    public boolean accountExistsInStorage(@NonNull String username) {
        return accountListDataElement.containsAccount(username);
    }

    /**
     * Represents an account list data element for pumpk1n<br>
     * <b>Do not access the accountUUIDs directly</b>
     */
    private static final class AccountListDataElement extends ParentedDataElement {

        @SuppressWarnings("FieldMayBeFinal")
        private Map<UUID, String> accountUUIDs = new HashMap<>();

        public AccountListDataElement() {
        }


        /**
         * Adds an account to the map
         *
         * @param uuid     The UUID of the account
         * @param username The username of the account
         *
         * @return True if the account was added, false otherwise
         */
        public synchronized boolean addAccount(UUID uuid, String username) {
            return accountUUIDs.put(uuid, username) == null; // null -> added
        }

        /**
         * Gets the username of an account with the given UUID
         *
         * @param username The username of the account
         *
         * @return The UUID of the account
         */
        public synchronized UUID getAccountUUID(String username) {
            return accountUUIDs.entrySet().stream()
                               .filter(entry -> entry.getValue().equals(username))
                               .map(Map.Entry::getKey)
                               .findFirst()
                               .orElse(null);
        }

        /**
         * Removes an account from the map
         *
         * @param uuid The UUID of the account
         *
         * @return True if the account was removed, false otherwise
         */
        public synchronized boolean removeAccount(UUID uuid) {
            return accountUUIDs.remove(uuid) != null; // not null -> removed
        }

        /**
         * Checks if the map contains an account with the given UUID
         *
         * @param uuid The UUID of the account
         *
         * @return True if the map contains the account, false otherwise
         */
        public synchronized boolean containsAccount(UUID uuid) {
            return accountUUIDs.containsKey(uuid);
        }

        /**
         * Checks if the map contains an account with the given username
         *
         * @param username The username of the account
         *
         * @return True if the map contains the account, false otherwise
         */
        public synchronized boolean containsAccount(String username) {
            return accountUUIDs.containsValue(username);
        }

        /**
         * Saves the data holder
         */
        public void save() {
            getDataHolderParent().save();
        }

        /**
         * Counts the number of accounts
         *
         * @return The number of accounts
         */
        public int countAccounts() {
            return accountUUIDs.size();
        }
    }

    /**
     * Represents an account data element for pumpk1n
     *
     * @param <T> The type of the account
     */
    private static final class AccountDataElement<T extends Account> extends ParentedDataElement {

        private T account;

        /**
         * Used for serialization.
         */
        public AccountDataElement() {
        }

        /**
         * Creates a new account holder with the given account.
         *
         * @param account The account
         */
        public AccountDataElement(T account) {
            this.account = account;
        }
    }
}
