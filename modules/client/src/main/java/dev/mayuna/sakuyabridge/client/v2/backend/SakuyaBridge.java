package dev.mayuna.sakuyabridge.client.v2.backend;

import dev.mayuna.sakuyabridge.client.v2.backend.enums.ConnectToServerResult;
import dev.mayuna.sakuyabridge.client.v2.backend.enums.ExchangeVersionResult;
import dev.mayuna.sakuyabridge.client.v2.backend.networking.Client;
import dev.mayuna.sakuyabridge.client.v2.backend.networking.results.RequestResult;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.SessionToken;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatMessage;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.timestop.networking.extension.CryptoKeyExchange;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The backend of SakuyaBridge: Time Stop
 */
@Getter
public final class SakuyaBridge {

    public static final SakuyaBridge INSTANCE = new SakuyaBridge();
    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridge.class);

    private final List<ChatRoom> activeChatRooms = new LinkedList<>();

    private Timer timer;

    private ClientConfig config;
    private Client client;

    private long lastPingIn;
    private long lastPingOut;

    private ServerInfo serverInfo;
    private int serverVersion;
    private int networkProtocol;

    private SessionToken currentSessionToken;
    private User user;

    private SakuyaBridge() {
    }

    /**
     * Used for debugging
     *
     * @param currentSessionToken The current session token
     */
    @Deprecated
    public void setCurrentSessionToken(SessionToken currentSessionToken) {
        this.currentSessionToken = currentSessionToken;
    }

    /**
     * Used for debugging
     *
     * @param user The user
     */
    @Deprecated
    public void setUser(User user) {
        this.user = user;
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
     * Prepares the ping timer task
     */
    private void preparePingTimerTask() {
        if (timer != null) {
            timer.cancel(); // Cancel timer
        }

        timer = new Timer(); // Create new instance of the timer

        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                if (!canRequestAuthenticated()) {
                    return;
                }

                final long sentMillis = System.currentTimeMillis();

                client.sendTCPWithResponse(new Packets.Requests.Ping(sentMillis), Packets.Responses.Pong.class, CommonConstants.PING_INTERVAL / 5, response -> {
                    final long receivedMillis = System.currentTimeMillis();
                    final long serverReceivedMillis = response.getSentTimestampMillis();

                    lastPingIn = (lastPingIn + receivedMillis - sentMillis) / 2;
                    lastPingOut = receivedMillis - serverReceivedMillis; // Useless... Basically measures the time difference between systems.
                }, () -> {
                    LOGGER.warn("Failed to ping server: Timeout");

                    lastPingIn = -1;
                    lastPingOut = -1;
                });
            }
        }, 0, CommonConstants.PING_INTERVAL + 100); // +100ms to be on the safe side
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
        user = null;
        lastPingIn = -1;
        lastPingOut = -1;
        activeChatRooms.clear();

        LOGGER.info("Preparing timer");
        preparePingTimerTask();

        if (client != null) {
            LOGGER.info("Stopping client");
            client.stop();
        }

        client = null;
    }

    /**
     * Handles a received chat message
     *
     * @param chatRoomName The chat room name
     * @param chatMessage  The chat message
     */
    public void receiveMessage(String chatRoomName, ChatMessage chatMessage) {
        synchronized (activeChatRooms) {
            activeChatRooms.stream().filter(chatRoom -> chatRoom.getName().equals(chatRoomName)).findFirst().ifPresent(chatRoom -> {
                chatRoom.addMessage(chatMessage);
            });
        }
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
     * Checks if the client can request (encrypted and authenticated)
     *
     * @return True if the client can request
     */
    private boolean canRequestAuthenticated() {
        return canRequestEncrypted() && currentSessionToken != null;
    }

    /**
     * Handles pre-request checks
     *
     * @param requestResultFuture The future to complete if the pre-request checks fail
     * @param <T>                 The type of the request result
     *
     * @return True if the pre-request checks pass
     */
    private <T> boolean handlePreRequest(CompletableFuture<RequestResult<T>> requestResultFuture) {
        if (!canRequestConnected()) {
            LOGGER.error("Cannot request: Not connected");
            requestResultFuture.complete(RequestResult.failure("Not connected"));
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
    private <T> boolean handleEncryptedPreRequest(CompletableFuture<RequestResult<T>> requestResultFuture) {
        if (!handlePreRequest(requestResultFuture)) {
            return false;
        }

        if (!canRequestEncrypted()) {
            LOGGER.error("Cannot request: Connection not encrypted");
            requestResultFuture.complete(RequestResult.failure("Connection not encrypted"));
            return false;
        }

        return true;
    }

    /**
     * Handles pre-request checks (connection must be encrypted and authenticated)
     *
     * @param requestResultFuture The future to complete if the pre-request checks fail
     * @param <T>                 The type of the request result
     *
     * @return True if the pre-request checks pass
     */
    private <T> boolean handleAuthenticatedPreRequest(CompletableFuture<RequestResult<T>> requestResultFuture) {
        if (!handleEncryptedPreRequest(requestResultFuture)) {
            return false;
        }

        if (!canRequestAuthenticated()) {
            LOGGER.error("Cannot request: Not authenticated");
            requestResultFuture.complete(RequestResult.failure("Not authenticated"));
            return false;
        }

        return true;
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
            LOGGER.error("Failed to connect to server: {} (hostname is empty)", host);
            return CompletableFuture.completedFuture(RequestResult.failure(ConnectToServerResult.INVALID_HOST, "Empty hostname"));
        }

        // Check if the port is invalid
        if (port < 0 || port > 65535) {
            LOGGER.error("Failed to connect to server: {} (invalid port)", host);
            return CompletableFuture.completedFuture(RequestResult.failure(ConnectToServerResult.INVALID_PORT, "Out of range port (0-65535)"));
        }

        // Create the future
        var future = new CompletableFuture<RequestResult<ConnectToServerResult>>();

        LOGGER.info("Connecting to server: {} on port {}", hostname, port);

        // Complete the future if the connection fails (e.g., gets disconnected sooner than crypto key exchange result)
        client.getConnectionSuccessful().whenCompleteAsync((success, throwable) -> {
            if (!success) {
                LOGGER.error("Failed to connect to server: {}", host, throwable);
                future.complete(RequestResult.failure(ConnectToServerResult.CONNECTION_FAILED, "Connection failed (" + throwable.getMessage() + ")"));
            }
        });

        // Connect to the server
        client.connectAsync(config.getConnectionTimeoutMillis(), hostname, port).whenCompleteAsync((connected, throwable) -> {
            if (throwable != null || !connected) {
                LOGGER.error("Failed to connect to server: {}", host, throwable);
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

        if (!handleEncryptedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Exchanging versions");

        // Send the version exchange request
        client.sendTCPWithResponse(new Packets.Requests.VersionExchange(CommonConstants.CURRENT_CLIENT_VERSION, CommonConstants.CURRENT_NETWORK_PROTOCOL), Packets.Responses.VersionExchange.class, response -> {
            this.serverVersion = response.getServerVersion();
            this.networkProtocol = response.getNetworkProtocol();

            LOGGER.info("Server Version Exchange:");
            LOGGER.info(" = Server Version: {}", this.serverVersion);
            LOGGER.info(" = Network Protocol: {} (expected: {})", CommonConstants.CURRENT_NETWORK_PROTOCOL, this.networkProtocol);

            // Check if the response has an error
            if (response.hasError()) {
                LOGGER.error("Failed version exchange: {}", response.getErrorMessage());
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

        if (!handleEncryptedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Fetching server info");

        // Send the server info request
        client.sendTCPWithResponse(new Packets.Requests.FetchServerInfo(), Packets.Responses.FetchServerInfo.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to fetch server info: {}", response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            this.serverInfo = response.getServerInfo();

            LOGGER.info("Successfully fetched server info:");
            LOGGER.info(" = UUID: {}", serverInfo.getUuid());
            LOGGER.info(" = Name: {}", serverInfo.getName());
            LOGGER.info(" = Region: {}", serverInfo.getRegion());
            LOGGER.info(" = Maintainer: {}", serverInfo.getMaintainer());
            LOGGER.info(" = MOTD: {}", serverInfo.getMotd());
            LOGGER.info(" = Authentication Methods: {}", serverInfo.getAuthenticationMethods());

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
        this.config.save();

        LoggedAccount loggedAccount = this.currentSessionToken.getLoggedAccount();

        LOGGER.info("Successfully logged in");
        LOGGER.info(" = Username: {}", loggedAccount.getUsername());
        LOGGER.info(" = UUID: {}", loggedAccount.getUuid());
        LOGGER.info(" = Session token expires: {} ({})", this.currentSessionToken.getExpirationTimePretty(), this.currentSessionToken.getExpirationTimeMillis());
    }

    /**
     * Login with the previous session token
     *
     * @return A future that completes when the login is successful (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<Packets.Responses.Auth.PreviousSessionLogin>> loginWithPreviousSession() {
        var future = new CompletableFuture<RequestResult<Packets.Responses.Auth.PreviousSessionLogin>>();

        if (!handleEncryptedPreRequest(future)) {
            return future;
        }

        if (config.isPreviousSessionTokenExpired()) {
            return CompletableFuture.completedFuture(RequestResult.failure("Previous session token expired"));
        }

        LOGGER.info("Logging in with previous session token");

        // Get the previous session token
        UUID previousSessionToken = config.getPreviousSessionToken().getToken();

        // Send the login request
        client.sendTCPWithResponse(new Packets.Requests.Auth.PreviousSessionLogin(previousSessionToken), Packets.Responses.Auth.PreviousSessionLogin.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to login with previous session: {}", response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
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
    public CompletableFuture<RequestResult<Packets.Responses.Auth.UsernamePasswordLogin>> loginWithUsernameAndPassword(final @NonNull String username, final char @NonNull [] password) {
        var future = new CompletableFuture<RequestResult<Packets.Responses.Auth.UsernamePasswordLogin>>();

        if (!handleEncryptedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Logging in with username '{}' and password", username);

        // Send the login request
        client.sendTCPWithResponse(new Packets.Requests.Auth.UsernamePasswordLogin(username, password), Packets.Responses.Auth.UsernamePasswordLogin.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to login with username and password: {}", response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
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
    public CompletableFuture<RequestResult<Packets.Responses.Auth.UsernamePasswordRegister>> registerWithUsernameAndPassword(final @NonNull String username, final char @NonNull [] password) {
        var future = new CompletableFuture<RequestResult<Packets.Responses.Auth.UsernamePasswordRegister>>();

        if (!handleEncryptedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Registering with username '{}' and password", username);

        // Send the register request
        client.sendTCPWithResponse(new Packets.Requests.Auth.UsernamePasswordRegister(username, password), Packets.Responses.Auth.UsernamePasswordRegister.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to register with username and password: " + response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
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

    /**
     * Fetches the current user
     *
     * @return A future that completes when the current user is fetched (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<User>> fetchCurrentUser() {
        var future = new CompletableFuture<RequestResult<User>>();

        if (!handleAuthenticatedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Fetching current user");

        // Send the fetch current user request
        client.sendTCPWithResponse(new Packets.Requests.FetchCurrentUser(), Packets.Responses.FetchCurrentUser.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to fetch current user: {}", response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            this.user = response.getUser();

            LOGGER.info("Successfully fetched current user:");
            LOGGER.info(" = Username: {}", this.user.getUsername());
            LOGGER.info(" = UUID: {}", this.user.getUuid());

            future.complete(RequestResult.success(this.user));
        }, () -> {
            LOGGER.error("Failed to fetch current user: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }

    /**
     * Fetches active chat rooms in which the user is in
     *
     * @return A future that completes when the chat rooms are fetched (will not be ever completed exceptionally)
     */
    public CompletableFuture<RequestResult<List<ChatRoom>>> fetchChatRooms() {
        var future = new CompletableFuture<RequestResult<List<ChatRoom>>>();

        /*
        // Mock
        synchronized (activeChatRooms) {
            activeChatRooms.clear();

            // mock
            activeChatRooms.add(new ChatRoom("General"));
            activeChatRooms.add(new ChatRoom("Random"));
            activeChatRooms.add(new ChatRoom("Some longer name (1)"));
            future.complete(RequestResult.success(activeChatRooms));

            if (true) {
                return future;
            }
        }
        */

        if (!handleAuthenticatedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Fetching chat rooms");

        // Send the fetch chat rooms request
        client.sendTCPWithResponse(new Packets.Requests.Chat.FetchChatRooms(), Packets.Responses.Chat.FetchChatRooms.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to fetch chat rooms: {}", response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            var chatRooms = response.getChatRooms();

            if (SakuyaBridge.INSTANCE.getConfig().getChatConfig().isKeepTheChatCivilWarning()) {
                chatRooms.forEach(chatRoom -> chatRoom.addMessage(new ChatMessage(CommonConstants.LOCAL_ACCOUNT, LanguageManager.INSTANCE.getTranslation(Lang.Other.TEXT_CHAT_KEEP_IT_CIVIL))));
            }

            synchronized (activeChatRooms) {
                this.activeChatRooms.clear();
                this.activeChatRooms.addAll(chatRooms);
            }

            LOGGER.info("Successfully fetched {} chat room(s)", activeChatRooms.size());
            future.complete(RequestResult.success(this.activeChatRooms));
        }, () -> {
            LOGGER.error("Failed to fetch chat rooms: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }

    /**
     * Sends a chat message to a chat room
     *
     * @param chatRoom The chat room
     * @param content  The content
     *
     * @return A future that completes when the chat message is sent (will not be ever completed exceptionally). The result of RequestResult is always
     * <code>null</code>.
     */
    public CompletableFuture<RequestResult<Void>> sendChatMessage(ChatRoom chatRoom, String content) {
        var future = new CompletableFuture<RequestResult<Void>>();

        if (!handleAuthenticatedPreRequest(future)) {
            return future;
        }

        LOGGER.info("Sending chat message to chat room '{}': '{}'", chatRoom.getName(), content);

        // Send the send chat message request
        client.sendTCPWithResponse(new Packets.Requests.Chat.SendChatMessage(chatRoom.getName(), content), Packets.Responses.Chat.SendChatMessage.class, response -> {
            if (response.hasError()) {
                LOGGER.error("Failed to send chat message: {}", response.getErrorMessage());
                future.complete(RequestResult.failure(response.getErrorMessage()));
                return;
            }

            LOGGER.info("Successfully sent chat message to chat room '{}'", chatRoom.getName());
            future.complete(RequestResult.success(null));
        }, () -> {
            LOGGER.error("Failed to send chat message: Timeout");
            future.complete(RequestResult.timeout());
        });

        return future;
    }
}
