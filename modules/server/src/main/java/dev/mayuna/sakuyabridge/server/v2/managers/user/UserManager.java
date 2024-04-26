package dev.mayuna.sakuyabridge.server.v2.managers.user;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.objects.users.StorageUserWrap;
import dev.mayuna.sakuyabridge.server.v2.util.pumpk1n.Pumpk1nLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages users
 */
public final class UserManager {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(UserManager.class);

    private final Config.UserManager config;
    private final List<StorageUserWrap> loadedUsers = new LinkedList<>();

    private Pumpk1n pumpk1n;

    /**
     * Creates a new user manager
     *
     * @param config The config
     */
    public UserManager(Config.UserManager config) {
        this.config = config;
    }

    /**
     * Initializes the user manager
     */
    public void init() {
        LOGGER.info("Initializing user manager...");

        initStorage();
    }

    /**
     * Initializes the storage
     */
    private void initStorage() {
        LOGGER.mdebug("Initializing storage...");

        pumpk1n = new Pumpk1n(config.getStorageSettings().createStorageHandler());

        // Create logger
        var logger = new Pumpk1nLogger(LOGGER, config.getStorageSettings().getLogLevel().getLog4jLevel());

        // Enable all pumpk1n logging, if desired
        if (config.getStorageSettings().isLogOperations()) {
            logger.enableAllLogs();
        }

        // Set logger
        pumpk1n.setLogger(logger);

        // Prepare storage
        pumpk1n.prepareStorage();
    }

    /**
     * Shuts down the user manager
     */
    public void shutdown() {
        saveStorage();
    }

    /**
     * Saves the users to storage
     */
    private void saveStorage() {
        LOGGER.mdebug("Saving users to storage...");

        synchronized (loadedUsers) {
            for (var user : loadedUsers) {
                user.getDataHolderParent().save();
            }

            LOGGER.mdebug("Saved {} users to the storage", loadedUsers.size());
        }
    }

    /**
     * Gets, loads or creates a user
     *
     * @param loggedAccount The logged account
     *
     * @return The user
     */
    public StorageUserWrap getLoadOrCreateUser(LoggedAccount loggedAccount) {
        var optionalUserLoaded = getUser(loggedAccount.getUuid());

        if (optionalUserLoaded.isPresent()) {
            return optionalUserLoaded.get();
        }

        var optionalUserInStorage = loadUser(loggedAccount);

        //noinspection OptionalIsPresent
        if (optionalUserInStorage.isPresent()) {
            return optionalUserInStorage.get();
        }

        return createUser(loggedAccount);
    }

    /**
     * Gets a user by account UUID from the loaded users
     *
     * @param accountUuid The account UUID
     *
     * @return Optional of the StorageUserWrap
     */
    private Optional<StorageUserWrap> getUser(UUID accountUuid) {
        synchronized (loadedUsers) {
            return loadedUsers.stream()
                              .filter(user -> user.getUser().getUuid().equals(accountUuid))
                              .findFirst();
        }
    }

    /**
     * Loads a user from storage<br>
     * If loaded, {@link LoggedAccount} is set to the loaded {@link StorageUserWrap}'s User
     *
     * @param loggedAccount Logged account
     *
     * @return The user
     */
    private Optional<StorageUserWrap> loadUser(LoggedAccount loggedAccount) {
        var dataHolder = pumpk1n.getOrLoadDataHolder(loggedAccount.getUuid());

        if (dataHolder == null) {
            return Optional.empty();
        }

        var userWrap = dataHolder.getDataElement(StorageUserWrap.class);

        if (userWrap == null) {
            return Optional.empty();
        }

        // Set the logged account
        userWrap.getUser().setLoggedAccount(loggedAccount);

        // Add to loaded users
        synchronized (loadedUsers) {
            loadedUsers.add(userWrap);
        }

        LOGGER.mdebug("Loaded user: {}", userWrap);

        return Optional.of(userWrap);
    }

    /**
     * Creates a user and saves it to storage<br>
     * Should be only called, when we are sure that the user does not exist in storage to
     * prevent deleted user data
     *
     * @param loggedAccount The logged account
     *
     * @return The user
     */
    private StorageUserWrap createUser(LoggedAccount loggedAccount) {
        var user = new User(loggedAccount);
        var userWrap = new StorageUserWrap(user);

        // Load the data holder
        var dataHolder = pumpk1n.getOrCreateDataHolder(loggedAccount.getUuid());
        dataHolder.addOrReplaceDataElement(userWrap);

        // Set the parent DataHolder
        userWrap.setDataHolderParent(dataHolder);

        // Save the user
        userWrap.getDataHolderParent().save();

        // Add to loaded users
        synchronized (loadedUsers) {
            loadedUsers.add(userWrap);
        }

        LOGGER.mdebug("Created user: {}", userWrap);

        return userWrap;
    }
}
