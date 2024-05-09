package dev.mayuna.sakuyabridge.commons.v2.networking.tcp;

import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.SessionToken;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.timestop.networking.timestop.TimeStopPackets;
import lombok.Getter;

import java.util.UUID;

/**
 * Sakuya Bridge packets
 */
@IgnoreNetworkRegistration(ignoreInnerClasses = false)
public final class Packets {

    private Packets() {
    }

    /**
     * Represents basic Sakuya Bridge packet
     */
    public static abstract class SakuyaBridgePacket extends TimeStopPackets.BasePacket {

        public SakuyaBridgePacket() {
        }
    }

    /**
     * Represents a packet that should not be logged (e.g., Ping packets)
     */
    public interface IgnoreLogging {
    }

    // ============= Requests ============= //

    /**
     * Represents the packet types (requests)
     */
    @IgnoreNetworkRegistration(ignoreInnerClasses = false)
    public static final class Requests {

        private Requests() {
        }

        /**
         * Ping packet
         */
        @Getter
        public static final class Ping extends SakuyaBridgePacket implements IgnoreLogging {

            private long sentTimestampMillis;

            /**
             * Used for serialization
             */
            public Ping() {
            }

            /**
             * Creates a new ping packet
             *
             * @param sentTimestampMillis The timestamp when the packet was sent
             */
            public Ping(long sentTimestampMillis) {
                this.sentTimestampMillis = sentTimestampMillis;
            }
        }

        /**
         * Requests the server info
         */
        public static final class FetchServerInfo extends SakuyaBridgePacket {

            public FetchServerInfo() {
            }
        }

        /**
         * Requests the server version.<br>
         * Also exchanges the client version.
         */
        @Getter
        public static final class VersionExchange extends SakuyaBridgePacket {

            private int clientVersion;
            private int networkProtocolVersion;

            public VersionExchange() {
            }

            public VersionExchange(int clientVersion, int networkProtocolVersion) {
                this.clientVersion = clientVersion;
                this.networkProtocolVersion = networkProtocolVersion;
            }
        }

        // <editor-fold desc="Authentication Packets">

        /**
         * Auth requests
         */
        @IgnoreNetworkRegistration(ignoreInnerClasses = false)
        public static final class Auth {

            /**
             * Requests to login using a previous session token
             */
            @Getter
            public static final class PreviousSessionLogin extends SakuyaBridgePacket {

                private UUID previousSessionToken;

                public PreviousSessionLogin() {
                }

                public PreviousSessionLogin(UUID previousSessionToken) {
                    this.previousSessionToken = previousSessionToken;
                }
            }

            /**
             * Requests to login using username and password
             */
            @Getter
            public static class UsernamePasswordLogin extends SakuyaBridgePacket {

                private String username;
                private char[] password;

                public UsernamePasswordLogin() {
                }

                public UsernamePasswordLogin(String username, char[] password) {
                    this.username = username;
                    this.password = password;
                }
            }

            /**
             * Requests to register using username and password
             */
            @Getter
            public static final class UsernamePasswordRegister extends UsernamePasswordLogin {

                public UsernamePasswordRegister() {
                }

                public UsernamePasswordRegister(String username, char[] password) {
                    super(username, password);
                }
            }
        }

        // </editor-fold>

        /**
         * Requests to fetch current user data
         */
        public static final class FetchCurrentUser extends SakuyaBridgePacket {

            /**
             * Used for serialization
             */
            public FetchCurrentUser() {
            }
        }
    }

    // ============= Responses ============= //

    /**
     * Represents the packet types (responses)
     */
    @IgnoreNetworkRegistration(ignoreInnerClasses = false)
    public static final class Responses {

        private Responses() {
        }

        /**
         * Response to the ping packet
         */
        @Getter
        public static final class Pong extends SakuyaBridgePacket implements IgnoreLogging {

            private long sentTimestampMillis;

            /**
             * Used for serialization
             */
            public Pong() {
            }

            /**
             * Creates a new ping packet
             *
             * @param sentTimestampMillis The timestamp when the packet was sent
             */
            public Pong(long sentTimestampMillis) {
                this.sentTimestampMillis = sentTimestampMillis;
            }
        }

        /**
         * Response to the server info request
         */
        @Getter
        public static final class FetchServerInfo extends SakuyaBridgePacket {

            private ServerInfo serverInfo;

            public FetchServerInfo() {
            }

            /**
             * Creates a new server info response
             *
             * @param serverInfo The server info
             */
            public FetchServerInfo(ServerInfo serverInfo) {
                this.serverInfo = serverInfo;
            }
        }

        /**
         * Response to the server version request
         */
        @Getter
        public static final class VersionExchange extends SakuyaBridgePacket {

            private int serverVersion;
            private int networkProtocol;

            public VersionExchange() {
            }

            public VersionExchange(int serverVersion, int networkProtocol) {
                this.serverVersion = serverVersion;
                this.networkProtocol = networkProtocol;
            }
        }

        // <editor-fold desc="Authentication Packets">

        /**
         * Auth responses
         */
        @IgnoreNetworkRegistration(ignoreInnerClasses = false)
        public static final class Auth {

            /**
             * Represents basic login response
             */
            @Getter
            private static abstract class Login extends SakuyaBridgePacket {

                protected SessionToken sessionToken;

                public Login() {
                }

                public Login(SessionToken sessionToken) {
                    this.sessionToken = sessionToken;
                }

                /**
                 * Sets the session token
                 *
                 * @param sessionToken The session token
                 *
                 * @return The login response
                 */
                public SakuyaBridgePacket withSessionToken(SessionToken sessionToken) {
                    this.sessionToken = sessionToken;
                    return this;
                }
            }

            /**
             * Response to the previous session login request
             */
            @Getter
            public static final class PreviousSessionLogin extends Login {

                public PreviousSessionLogin() {
                }

                public PreviousSessionLogin(SessionToken sessionToken) {
                    super(sessionToken);
                }
            }

            /**
             * Response to the username and password login request
             */
            @Getter
            public static class UsernamePasswordLogin extends Login {

                public UsernamePasswordLogin() {
                }

                public UsernamePasswordLogin(SessionToken sessionToken) {
                    super(sessionToken);
                }
            }

            /**
             * Response to the username and password register request
             */
            @Getter
            public static final class UsernamePasswordRegister extends UsernamePasswordLogin {

                public UsernamePasswordRegister() {
                }

                public UsernamePasswordRegister(SessionToken sessionToken) {
                    super(sessionToken);
                }
            }
        }

        // </editor-fold>

        /**
         * Response to fetch current user data
         */
        @Getter
        public static final class FetchCurrentUser extends SakuyaBridgePacket {

            private User user;

            /**
             * Used for serialization
             */
            public FetchCurrentUser() {
            }

            /**
             * Creates a new fetch user response
             *
             * @param user The user
             */
            public FetchCurrentUser(User user) {
                this.user = user;
            }
        }
    }

    // ============= Notifications ============= //

    /**
     * Represents the packet types (notifications - do not require a response)
     */
    @IgnoreNetworkRegistration(ignoreInnerClasses = false)
    public static final class Notifications {

        private Notifications() {
        }
    }
}
