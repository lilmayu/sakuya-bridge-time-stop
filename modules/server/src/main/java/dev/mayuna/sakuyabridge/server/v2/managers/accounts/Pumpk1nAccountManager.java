package dev.mayuna.sakuyabridge.server.v2.managers.accounts;

import com.google.gson.*;
import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.api.Migratable;
import dev.mayuna.pumpk1n.api.ParentedDataElement;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.util.pumpk1n.Pumpk1nLogger;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A manager for accounts (data storage managed by Pumpk1n)
 *
 * @param <T> The type of the account
 */
public abstract class Pumpk1nAccountManager<T extends Account> extends AccountManager<T> {

    private final SakuyaBridgeLogger logger;
    private final Level logLevel;
    private final Pumpk1n pumpk1n;

    @Getter
    private AccountListDataElement accountListDataElement = new AccountListDataElement();

    /**
     * Creates a new account manager
     *
     * @param logger   The logger
     * @param logLevel The log level
     * @param pumpk1n  The pumpk1n instance
     */
    public Pumpk1nAccountManager(SakuyaBridgeLogger logger, Level logLevel, Pumpk1n pumpk1n) {
        this.logger = logger;
        this.logLevel = logLevel;
        this.pumpk1n = pumpk1n;
    }

    /**
     * Enables logging with the current logger and current log level
     */
    public void enablePumpk1nLogging() {
        pumpk1n.setLogger(new Pumpk1nLogger(logger, logLevel));
    }

    /**
     * Enables logging with the current logger and the given level
     *
     * @param level The level
     */
    public void enablePumpk1nLogging(Level level) {
        pumpk1n.setLogger(new Pumpk1nLogger(logger, level));
    }

    /**
     * Enables logging with the given logger and the given level
     *
     * @param logger The logger
     * @param level  The level
     */
    public void enablePumpk1nLogging(SakuyaBridgeLogger logger, Level level) {
        pumpk1n.setLogger(new Pumpk1nLogger(logger, level));
    }

    /**
     * Enables all pumpk1n logs if the pumpk1n logger is a Pumpk1nLogger
     */
    public void enableAllPumpk1nLogs() {
        if (pumpk1n.getLogger() instanceof Pumpk1nLogger pumpk1nLogger) {
            pumpk1nLogger.enableAllLogs();
        }
    }

    /**
     * Prepares the account manager
     */
    @Override
    public void prepare() {
        // Prepare pumpk1n
        pumpk1n.prepareStorage();

        // Load the account list data element
        var dataHolder = pumpk1n.getOrCreateDataHolder(ServerConstants.MAIN_DATA_HOLDER_UUID);
        accountListDataElement = dataHolder.getOrCreateDataElement(AccountListDataElement.class);

        logger.log(logLevel, "Loaded account list data element (" + accountListDataElement.countAccounts() + " accounts)");
    }

    /**
     * Recreates the account list from the storage
     */
    public void recreateAccountList() {
        if (!(pumpk1n.getStorageHandler() instanceof Migratable migratableStorageHandler)) {
            logger.log(logLevel, "Storage handler is not migratable (cannot recreate whole account list)");
            return;
        }


        logger.log(logLevel, "Recreating account list");
        pumpk1n.deleteDataHolder(ServerConstants.MAIN_DATA_HOLDER_UUID);

        AccountListDataElement newAccountListDataElement = new AccountListDataElement();

        for (UUID dataHolderUUID : migratableStorageHandler.getAllHolderUUIDs()) {
            var dataHolder = pumpk1n.getOrLoadDataHolder(dataHolderUUID);
            var dataElement = dataHolder.getDataElement(AccountDataElement.class);

            if (dataElement == null) {
                continue; // Skip, might by the main data holder
            }

            logger.log(logLevel, "Recreating account: " + dataElement.account);
            newAccountListDataElement.addAccount(dataElement.account.getUuid(), dataElement.account.getUsername());
        }

        var mainDataHolder = pumpk1n.getOrCreateDataHolder(ServerConstants.MAIN_DATA_HOLDER_UUID);
        mainDataHolder.addOrReplaceDataElement(newAccountListDataElement);
        mainDataHolder.save();

        logger.log(logLevel, "Recreated account list with " + newAccountListDataElement.countAccounts() + " accounts");
    }

    /**
     * Shuts down the account manager
     */
    @Override
    public void shutdown() {
        super.shutdown();

        // Save the account list data element
        accountListDataElement.save();

        logger.log(logLevel, "Saved account list data element (" + accountListDataElement.countAccounts() + " accounts)");
    }

    /**
     * Deletes an account from the database
     *
     * @param username The username of the account
     *
     * @return Whether the account was deleted
     */
    @Override
    public boolean deleteAccount(@NonNull String username) {
        var uuid = accountListDataElement.getAccountUUID(username);

        if (uuid == null) {
            return false;
        }

        return deleteAccount(uuid);
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

        // Remove the account from the account list
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

        // Remove the account from the memory
        super.removeAccount(uuid);

        // Log
        if (deletedSomething) {
            logger.log(logLevel, "Deleted account with UUID " + uuid);
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

        logger.log(logLevel, "Saved account " + account);
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

        // Add the account to the memory
        super.addAccount(account);

        logger.log(logLevel, "Loaded account " + account);

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
    public static final class AccountListDataElement extends ParentedDataElement {

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
    public static final class AccountDataElement<T extends Account> extends ParentedDataElement {

        private transient final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(AccountDataElement.class, new AccountDataElementTypeAdapter<T>());

        private Class<T> accountClass;
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
            this.accountClass = (Class<T>) account.getClass();
            this.account = account;
        }

        @Override
        public @NonNull GsonBuilder getGsonBuilder() {
            return gsonBuilder;
        }
    }

    private static final class AccountDataElementTypeAdapter<T extends Account> implements JsonSerializer<AccountDataElement<T>>, JsonDeserializer<AccountDataElement<T>> {

        @Override
        public AccountDataElement<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Class<T> accountClass;
            String accountClassName = jsonObject.get("accountClassName").getAsString();

            try {
                accountClass = (Class<T>) Class.forName(accountClassName);
            } catch (Exception exception) {
                throw new RuntimeException("Failed to deserialize accountClassName: " + accountClassName, exception);
            }

            T account = context.deserialize(jsonObject.get("account"), accountClass);

            return new AccountDataElement<>(account);
        }

        @Override
        public JsonElement serialize(AccountDataElement<T> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("accountClassName", src.accountClass.getName());
            jsonObject.add("account", context.serialize(src.account));
            return jsonObject;
        }
    }
}
