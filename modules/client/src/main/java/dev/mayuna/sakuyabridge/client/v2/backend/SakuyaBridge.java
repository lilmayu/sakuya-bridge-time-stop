package dev.mayuna.sakuyabridge.client.v2.backend;

import dev.mayuna.sakuyabridge.client.v2.backend.enums.ConnectToServerResult;
import dev.mayuna.sakuyabridge.client.v2.backend.enums.ExchangeVersionResult;
import dev.mayuna.sakuyabridge.client.v2.backend.networking.Client;
import dev.mayuna.sakuyabridge.client.v2.backend.networking.results.RequestResult;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.SessionToken;
import dev.mayuna.timestop.networking.extension.CryptoKeyExchange;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The backend of SakuyaBridge: Time Stop
 */
@Getter
public final class SakuyaBridge {

    public static final SakuyaBridge INSTANCE = new SakuyaBridge();
    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridge.class);

    private ClientConfig config;
    private Client client;

    private ServerInfo serverInfo;
    private int serverVersion;
    private int networkProtocol;

    private SessionToken currentSessionToken;

    private SakuyaBridge() {
    }

    /**
     * Starts the backend
     */
    public void boot() {
        LOGGER.info("Booting SakuyaBridge");
        reset();

        config = ClientConfig.load();
        config.clearPreviousSessionTokenIfExpired();
    }

    /**
     * Prepares the backend for connection
     */
    public boolean prepareForConnect() {
        LOGGER.info("Preparing SakuyaBridge for connection");
        reset();

        client = new Client(config);
        client.start();

        if (!client.isSuccessfullyPrepared()) {
            LOGGER.error("Failed to prepare client");
            return false;
        }

        return true;
    }

    /**
     * Resets the backend
     */
    public void reset() {
        LOGGER.info("Resetting SakuyaBridge");

        serverInfo = null;
        serverVersion = -1;
        networkProtocol = -1;
        currentSessionToken = null;

        if (client != null) {
            LOGGER.info("Stopping client");
            client.stop();
        }

        client = null;
    }

    /**
     * Returns the logged account
     *
     * @return The logged account (null if not logged in)
     */
    public LoggedAccount getLoggedAccount() {
        return currentSessionToken != null ? currentSessionToken.getLoggedAccount() : null;
    }

    /**
     * Checks if the client can request
     *
     * @return True if the client can request
     */
    private boolean canRequestConnected() {
        return client != null && client.isConnected();
    }

    /**
     * Checks if the client can request (encrypted)
     *
     * @return True if the client can request
     */
    private boolean canRequestEncrypted() {
        return canRequestConnected() && client.isEncryptTraffic();
    }

    /**
     * Handles pre-request checks
     *
     * @param requestResultFuture         The future to complete if the pre-request checks fail
     * @param shouldBeConnectionEncrypted If the connection should be encrypted
     * @param <T>                         The type of the request result
     *
     * @return True if the pre-request checks pass
     */
    private <T> boolean handlePreRequest(CompletableFuture<RequestResult<T>> requestResultFuture, boolean shouldBeConnectionEncrypted) {
        if (!canRequestConnected()) {
            LOGGER.error("Cannot request: Not connected");
            requestResultFuture.complete(RequestResult.failure("Not connected"));
            return false;
        }

        if (shouldBeConnectionEncrypted && !canRequestEncrypted()) {
            LOGGER.error("Cannot request: Connection not encrypted");
            requestResultFuture.complete(RequestResult.failure("Connection not encrypted"));
            return false;
        }

        return true;
    }

    /**
     * Handles pre-request checks (connection must be encrypted)
     *
     * @param requestResultFuture The future to complete if the pre-request checks fail
     * @param <T>                 The type of the request result
     *
     * @return True if the pre-request checks pass
     */
    private <T> boolean handlePreRequest(CompletableFuture<RequestResult<T>> requestResultFuture) {
        return handlePreRequest(requestResultFuture, true);
    }

    /**
     * Connects to the server
     *
     * @param host The host to connect to
     *
     * @return A future that completes when the connection is established (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<ConnectToServerResult>> connectToServer(final @NonNull String host) {
        String hostname = host;
        int port = CommonConstants.DEFAULT_PORT;

        // Check if the host contains a port
        if (host.contains(":")) {
            String[] split = host.split(":");
            hostname = split[0];
            port = Integer.parseInt(split[1]);
        }

        // Check if the hostname is empty
        if (hostname.isEmpty()) {
            LOGGER.error("Failed to connect to server: " + host + " (hostname is empty)");
            return CompletableFuture.completedFuture(RequestResult.failure(ConnectToServerResult.INVALID_HOST, "Empty hostname"));
        }

        // Check if the port is invalid
        if (port < 0 || port > 65535) {
            LOGGER.error("Failed to connect to server: " + host + " (invalid port)");
            return CompletableFuture.completedFuture(RequestResult.failure(ConnectToServerResult.INVALID_PORT, "Out of range port (0-65535)"));
        }

        // Create the future
        var future = new CompletableFuture<RequestResult<ConnectToServerResult>>();

        LOGGER.info("Connecting to server: " + hostname + " on port " + port);

        // Complete the future if the connection fails (e.g., gets disconnected sooner than crypto key exchange result)
        client.getConnectionSuccessful().whenCompleteAsync((success, throwable) -> {
            if (!success) {
                LOGGER.error("Failed to connect to server: " + host, throwable);
                future.complete(RequestResult.failure(ConnectToServerResult.CONNECTION_FAILED, "Connection failed (" + throwable.getMessage() + ")"));
            }
        });

        // Connect to the server
        client.connectAsync(config.getConnectionTimeoutMillis(), hostname, port).whenCompleteAsync((connected, throwable) -> {
            if (throwable != null || !connected) {
                LOGGER.error("Failed to connect to server: " + host, throwable);
                future.complete(RequestResult.failure(ConnectToServerResult.CONNECTION_FAILED, "Connection failed (" + (throwable != null ? throwable.getMessage() : "Unknown error") + ")"));
                return;
            }

            LOGGER.success("Successfully connected to the server");
            LOGGER.info("Creating encrypted communication...");

            try {
                // Run crypto key exchange
                new CryptoKeyExchange.ClientTask(client.getEncryptionManager()).run(client);
            } catch (Exception exception) {
                if (client.getConnectionSuccessful().isDone() && !client.getConnectionSuccessful().join()) {
                    return; // Silently ignore if the connection is failed
                }

                LOGGER.error("Failed to create encrypted communication", exception);
                future.complete(RequestResult.failure(ConnectToServerResult.ENCRYPTED_COMMUNICATION_FAILED, "Failed to create encrypted communication (" + exception.getMessage() + ")"));
                return;
            }

            LOGGER.success("Successfully created encrypted communication");

            // Set the client to encrypt traffic
            client.setEncryptTraffic(true);
            future.complete(RequestResult.success(ConnectToServerResult.SUCCESSFUL));
            client.getConnectionSuccessful().complete(true); // So we don't have hanging futures
        });

        return future;
    }

    /**
     * Fetches the server version from the SakuyaBridge server
     *
     * @return A future that completes when the server version is fetched (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<ExchangeVersionResult>> exchangeVersions() {
        var future = new CompletableFuture<RequestResult<ExchangeVersionResult>>();

        if (!handlePreRequest(future)) {
            return future;
        }

        LOGGER.info("Exchanging versions");

        // Send the version exchange request
        client.sendTCPWithResponse(new Packets.Requests.VersionExchange(CommonConstants.CURRENT_CLIENT_VERSION, CommonConstants.CURRENT_NETWORK_PROTOCOL), Packets.Responses.VersionExchange.class, response -> {
            this.serverVersion = response.getServerVersion();
            this.networkProtocol = response.getNetworkProtocol();

            LOGGER.info("Server Version Exchange:");
            LOGGER.info(" = Server Version: " + this.serverVersion);
            LOGGER.info(" = Network Protocol: " + this.networkProtocol + " (expected: " + CommonConstants.CURRENT_NETWORK_PROTOCOL + ")");

            // Check if the response has an error
            if (response.hasError()) {
                LOGGER.error("Failed version exchange: " + response.getErrorMessage());
                future.complete(RequestResult.failure(ExchangeVersionResult.UNSUPPORTED, response.getErrorMessage()));
                return;
            }

            LOGGER.success("Successfully exchanged versions");

            future.complete(RequestResult.success(ExchangeVersionResult.SUPPORTED.withServerVersion(response.getServerVersion())
                                                                                 .withNetworkProtocol(response.getNetworkProtocol())));
        }, () -> {
            LOGGER.error("Failed to exchange versions: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }

    /**
     * Fetches the server info from the SakuyaBridge server
     *
     * @return A future that completes when the server info is fetched (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<ServerInfo>> fetchServerInfo() {
        var future = new CompletableFuture<RequestResult<ServerInfo>>();

        if (!handlePreRequest(future)) {
            return future;
        }

        LOGGER.info("Fetching server info");

        // Send the server info request
        client.sendTCPWithResponse(new Packets.Requests.ServerInfo(), Packets.Responses.ServerInfo.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to fetch server info: " + response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            this.serverInfo = response.getServerInfo();

            LOGGER.info("Successfully fetched server info:");
            LOGGER.info(" = UUID: " + serverInfo.getUuid());
            LOGGER.info(" = Name: " + serverInfo.getName());
            LOGGER.info(" = Region: " + serverInfo.getRegion());
            LOGGER.info(" = Maintainer: " + serverInfo.getMaintainer());
            LOGGER.info(" = MOTD: " + serverInfo.getMotd());
            LOGGER.info(" = Authentication Methods: " + serverInfo.getAuthenticationMethods());

            future.complete(RequestResult.success(serverInfo));
        }, () -> {
            LOGGER.error("Failed to fetch server info: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }

    /**
     * Handles a successful login
     *
     * @param sessionToken The session token
     */
    private void handleSuccessfulLogin(SessionToken sessionToken) {
        this.currentSessionToken = sessionToken;
        this.config.setPreviousSessionToken(this.currentSessionToken);

        LoggedAccount loggedAccount = this.currentSessionToken.getLoggedAccount();

        LOGGER.info("Successfully logged in");
        LOGGER.info(" = Username: " + loggedAccount.getUsername());
        LOGGER.info(" = UUID: " + loggedAccount.getUuid());
        LOGGER.info(" = Session token expires: " + this.currentSessionToken.getExpirationTimePretty() + " (" + this.currentSessionToken.getExpirationTimeMillis() + ")");
    }

    /**
     * Login with the previous session token
     *
     * @return A future that completes when the login is successful (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<Packets.Responses.PreviousSessionLogin>> loginWithPreviousSession() {
        var future = new CompletableFuture<RequestResult<Packets.Responses.PreviousSessionLogin>>();

        if (!handlePreRequest(future)) {
            return future;
        }

        if (config.isPreviousSessionTokenExpired()) {
            return CompletableFuture.completedFuture(RequestResult.failure("Previous session token expired"));
        }

        LOGGER.info("Logging in with previous session token");

        // Get the previous session token
        UUID previousSessionToken = config.getPreviousSessionToken().getToken();

        // Send the login request
        client.sendTCPWithResponse(new Packets.Requests.PreviousSessionLogin(previousSessionToken), Packets.Responses.PreviousSessionLogin.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to login with previous session: " + response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            // Sanity check, should never happen
            if (!response.isSuccess()) {
                LOGGER.warn("Failed to login with previous session but no error message was supplied (bug?)");
                future.complete(RequestResult.failure("Unknown error (bug?)"));
                return;
            }

            handleSuccessfulLogin(response.getSessionToken());

            future.complete(RequestResult.success(response));
        }, () -> {
            LOGGER.error("Failed to login with previous session: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }

    /**
     * Logs in with a username and password
     *
     * @param username The username
     * @param password The password
     *
     * @return A future that completes when the login is successful (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<Packets.Responses.UsernamePasswordLogin>> loginWithUsernameAndPassword(final @NonNull String username, final char @NonNull [] password) {
        var future = new CompletableFuture<RequestResult<Packets.Responses.UsernamePasswordLogin>>();

        if (!handlePreRequest(future)) {
            return future;
        }

        LOGGER.info("Logging in with username '" + username + "' and password");

        // Send the login request
        client.sendTCPWithResponse(new Packets.Requests.UsernamePasswordLogin(username, password), Packets.Responses.UsernamePasswordLogin.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to login with username and password: " + response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            // Sanity check, should never happen
            if (!response.isSuccess()) {
                LOGGER.warn("Failed to login with username and password but no error message was supplied (bug?)");
                return;
            }

            handleSuccessfulLogin(response.getSessionToken());

            future.complete(RequestResult.success(response));
        }, () -> {
            LOGGER.error("Failed to login with username and password: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }

    /**
     * Registers with a username and password
     *
     * @param username The username
     * @param password The password
     *
     * @return A future that completes when the registration is successful (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<Packets.Responses.UsernamePasswordRegister>> registerWithUsernameAndPassword(final @NonNull String username, final char @NonNull [] password) {
        var future = new CompletableFuture<RequestResult<Packets.Responses.UsernamePasswordRegister>>();

        if (!handlePreRequest(future)) {
            return future;
        }

        LOGGER.info("Registering with username '" + username + "' and password");

        // Send the register request
        client.sendTCPWithResponse(new Packets.Requests.UsernamePasswordRegister(username, password), Packets.Responses.UsernamePasswordRegister.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to register with username and password: " + response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            // Sanity check, should never happen
            if (!response.isSuccess()) {
                LOGGER.warn("Failed to register with username and password but no error message was supplied (bug?)");
                return;
            }

            handleSuccessfulLogin(response.getSessionToken());

            future.complete(RequestResult.success(response));
        }, () -> {
            LOGGER.error("Failed to register with username and password: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }
}
