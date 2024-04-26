package dev.mayuna.sakuyabridge.server.v2.managers.sessions;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.api.ParentedDataElement;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.SessionToken;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.managers.accounts.Pumpk1nAccountManager;
import dev.mayuna.sakuyabridge.server.v2.util.pumpk1n.Pumpk1nLogger;
import lombok.NonNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * Manages session tokens
 */
public final class SessionTokenManager {

    public static final long SESSION_CLEANUP_INTERVAL = 1000 * 60 * 5; // 5 minutes

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SessionTokenManager.class);

    private final Timer sessionCleanupTimer = new Timer("Session Token Cleanup", false);
    private final Config.SessionTokenManager config;

    private Pumpk1n pumpk1n;

    private SessionListDataElement activeSessions = new SessionListDataElement();

    /**
     * Creates a new session manager
     *
     * @param config The config
     */
    public SessionTokenManager(Config.SessionTokenManager config) {
        this.config = config;
    }

    /**
     * Initializes the session manager
     */
    public void init() {
        LOGGER.info("Initializing session token manager...");

        LOGGER.mdebug("Initializing storage...");
        initStorage();

        LOGGER.mdebug("Loading previous session tokens from storage");
        loadFromStorage();

        LOGGER.mdebug("Scheduling session cleanup & saving timer");
        sessionCleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanupExpiredSessions();
            }
        }, 0, SESSION_CLEANUP_INTERVAL);
    }

    /**
     * Shuts down the session manager
     */
    public void shutdown() {
        LOGGER.info("Shutting down session token manager...");

        LOGGER.mdebug("Saving session tokens to storage");
        saveToStorage();

        LOGGER.mdebug("Cancelling session cleanup timer");
        sessionCleanupTimer.cancel();
    }

    /**
     * Initializes the storage
     */
    private void initStorage() {
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
     * Loads previous session tokens from storage
     */
    private void loadFromStorage() {
        var dataHolder = pumpk1n.getOrCreateDataHolder(ServerConstants.MAIN_DATA_HOLDER_UUID);
        this.activeSessions = dataHolder.getOrCreateDataElement(SessionListDataElement.class);

        LOGGER.mdebug("Loaded " + activeSessions.countSessionTokens() + " session tokens from storage");
    }

    /**
     * Saves session tokens to storage
     */
    private void saveToStorage() {
        activeSessions.save();

        LOGGER.mdebug("Saved " + activeSessions.countSessionTokens() + " session tokens to storage");
    }

    /**
     * Renews, gets or creates a session token for an account
     *
     * @param account The account
     *
     * @return The renewed session token
     */
    public SessionToken renewGetOrCreateSessionToken(@NonNull Account account) {
        Optional<SessionToken> existingSessionToken = getSessionToken(account.getUuid());

        if (existingSessionToken.isPresent()) {
            SessionToken sessionToken = existingSessionToken.get();
            renewSessionToken(sessionToken);
            return sessionToken;
        }

        return getOrCreateSessionToken(account);
    }

    /**
     * Renews a session token
     *
     * @param sessionToken The session token
     */
    public void renewSessionToken(@NonNull SessionToken sessionToken) {
        sessionToken.setExpirationTimeMillis(System.currentTimeMillis() + config.getSessionLifespanMillis());
        activeSessions.save();

        LOGGER.mdebug("Renewed session token for account " + sessionToken.getLoggedAccount());
    }

    /**
     * Gets or creates a session token for an account
     *
     * @param account The account
     *
     * @return The session token
     */
    private SessionToken getOrCreateSessionToken(@NonNull Account account) {
        Optional<SessionToken> existingSessionToken = getSessionToken(account.getUuid());

        if (existingSessionToken.isPresent()) {
            return existingSessionToken.get();
        }

        SessionToken sessionToken = SessionToken.create(account, System.currentTimeMillis() + config.getSessionLifespanMillis());

        activeSessions.add(sessionToken);
        activeSessions.save();

        LOGGER.mdebug("Created session token for account " + account);

        return sessionToken;
    }

    /**
     * Gets a session token for an account<br>
     * Returns an empty optional if the session token is expired or not found
     *
     * @param accountUuid The account UUID
     *
     * @return Optional of session token
     */
    public Optional<SessionToken> getSessionToken(@NonNull UUID accountUuid) {
        return activeSessions.getSessionToken(accountUuid);
    }

    /**
     * Gets a session token by its token<br>
     * Returns an empty optional if the session token is expired or not found
     *
     * @param token The token
     *
     * @return The session token
     */
    public Optional<SessionToken> getSessionTokenByTokenUuid(@NonNull UUID token) {
        return activeSessions.getSessionTokenByTokenUuid(token);
    }

    /**
     * Cleans up expired sessions
     */
    private void cleanupExpiredSessions() {
        activeSessions.removeIf(sessionToken -> {
            if (sessionToken.isExpired()) {
                LOGGER.mdebug("Removed expired session token for account " + sessionToken.getLoggedAccount());
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Data element for the list of session tokens
     */
    public static final class SessionListDataElement extends ParentedDataElement {

        /**
         * When accessing the list, synchronize on this object
         */
        private final static Object lock = new Object();

        @SuppressWarnings("FieldMayBeFinal")
        private List<SessionToken> activeSessions = new LinkedList<>();

        /**
         * Saves the data holder
         */
        public void save() {
            synchronized (lock) {
                this.getDataHolderParent().save();
            }
        }

        /**
         * Counts the number of active session tokens
         *
         * @return The number of active session tokens
         */
        public int countSessionTokens() {
            synchronized (lock) {
                return activeSessions.size();
            }
        }

        /**
         * Adds a session token
         *
         * @param sessionToken The session token
         */
        public void add(SessionToken sessionToken) {
            synchronized (lock) {
                activeSessions.add(sessionToken);
            }
        }

        /**
         * Gets a session token for an account<br>
         * Returns an empty optional if the session token is expired or not found
         *
         * @param accountUuid The account UUID
         *
         * @return Optional of session token
         */
        public Optional<SessionToken> getSessionToken(@NonNull UUID accountUuid) {
            synchronized (SessionListDataElement.lock) {
                return activeSessions.stream()
                                     .filter(sessionToken -> !sessionToken.isExpired() && sessionToken.getLoggedAccount()
                                                                                                      .getUuid()
                                                                                                      .equals(accountUuid))
                                     .findFirst();
            }
        }

        /**
         * Gets a session token by its token<br>
         * Returns an empty optional if the session token is expired or not found
         *
         * @param token The token
         *
         * @return The session token
         */
        public Optional<SessionToken> getSessionTokenByTokenUuid(@NonNull UUID token) {
            synchronized (lock) {
                return activeSessions.stream()
                                     .filter(sessionToken -> !sessionToken.isExpired() && sessionToken.getToken().equals(token))
                                     .findFirst();
            }
        }

        /**
         * Invokes the removeIf method on the list
         *
         * @param predicate The predicate
         *
         * @return Whether any elements were removed
         */
        @SuppressWarnings("UnusedReturnValue")
        public boolean removeIf(Predicate<SessionToken> predicate) {
            synchronized (lock) {
                return activeSessions.removeIf(predicate);
            }
        }
    }
}
