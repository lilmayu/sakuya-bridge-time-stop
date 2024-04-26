package dev.mayuna.sakuyabridge.server.v2.managers.accounts;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public final class AccountManagerBundle {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(AccountManagerBundle.class);

    private UsernamePasswordAccountManager usernamePassword;

    private List<AccountManager<?>> accountManagers;

    private final Config.AccountManager config;

    /**
     * Creates a new account manager bundle.
     */
    public AccountManagerBundle(Config.AccountManager config) {
        this.config = config;
    }

    /**
     * Initializes the account manager bundle.
     */
    public void init() {
        Pumpk1n pumpk1n = new Pumpk1n(config.getStorageSettings().createStorageHandler());

        // Init account managers
        LOGGER.info("Initializing username-password account manager");
        usernamePassword = new UsernamePasswordAccountManager(config.getStorageSettings().getLogLevel().getLog4jLevel(), pumpk1n);
        usernamePassword.enablePumpk1nLogging();

        // Init list of account managers
        accountManagers = List.of(usernamePassword);

        // Enable all pumpk1n logs, if desired
        if (config.getStorageSettings().isLogOperations()) {
            for (AccountManager<?> accountManager : accountManagers) {
                if (accountManager instanceof Pumpk1nAccountManager<?> pumpk1nAccountManager) {
                    pumpk1nAccountManager.enableAllPumpk1nLogs();
                }
            }
        }

        // Prepare
        LOGGER.info("Preparing account managers");
        accountManagers.forEach(AccountManager::prepare);

        if (config.isRecreatePumpk1nAccountListOnStartup()) {
            LOGGER.info("Recreating account list on startup for Pumpk1n Account Managers");
            accountManagers.forEach(accountManager -> {
                if (accountManager instanceof Pumpk1nAccountManager<?> pumpk1nAccountManager) {
                    pumpk1nAccountManager.recreateAccountList();
                }
            });
        }
    }

    /**
     * Shuts down the account manager bundle.
     */
    public void shutdown() {
        LOGGER.info("Shutting down account managers");
        accountManagers.forEach(AccountManager::shutdown);
    }

    /**
     * Gets the account with the given UUID.
     *
     * @param uuid The UUID
     *
     * @return Optional of account
     */
    public Optional<Account> getAccount(UUID uuid) {
        return Optional.ofNullable(accountManagers.stream()
                                                  .map(manager -> manager.getAccount(uuid))
                                                  .filter(Optional::isPresent)
                                                  .map(Optional::get)
                                                  .findFirst().orElse(null));
    }

    /**
     * Gets the account with the given username.
     *
     * @param username The username
     *
     * @return Optional of account
     */
    public Optional<Account> getAccount(String username) {
        return Optional.ofNullable(accountManagers.stream()
                                                  .map(manager -> manager.getAccount(username))
                                                  .filter(Optional::isPresent)
                                                  .map(Optional::get)
                                                  .findFirst().orElse(null));
    }
}
