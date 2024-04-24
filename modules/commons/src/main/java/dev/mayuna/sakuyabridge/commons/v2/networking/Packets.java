package dev.mayuna.sakuyabridge.commons.v2.networking;

import dev.mayuna.sakuyabridge.commons.v2.objects.auth.SessionToken;
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

    // ============= Requests ============= //

    /**
     * Represents the packet types (requests)
     */
    @IgnoreNetworkRegistration(ignoreInnerClasses = false)
    public static final class Requests {

        private Requests() {
        }

        /**
         * Requests the server info
         */
        public static final class ServerInfo extends SakuyaBridgePacket {

            public ServerInfo() {
            }
        }

        /**
         * Requests the server version.<br>
         * Also exchanges the client version.
         */
        @Getter
        public static class VersionExchange extends SakuyaBridgePacket {

            private int clientVersion;
            private int networkProtocolVersion;

            public VersionExchange() {
            }

            public VersionExchange(int clientVersion, int networkProtocolVersion) {
                this.clientVersion = clientVersion;
                this.networkProtocolVersion = networkProtocolVersion;
            }
        }

        /**
         * Requests to login using a previous session token
         */
        @Getter
        public static class PreviousSessionLogin extends SakuyaBridgePacket {

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
        public static class UsernamePasswordRegister extends UsernamePasswordLogin {

            public UsernamePasswordRegister() {
            }

            public UsernamePasswordRegister(String username, char[] password) {
                super(username, password);
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
         * Response to the server info request
         */
        @Getter
        public static final class ServerInfo extends SakuyaBridgePacket {

            private dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo serverInfo;

            public ServerInfo() {
            }

            /**
             * Creates a new server info response
             *
             * @param serverInfo The server info
             */
            public ServerInfo(dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo serverInfo) {
                this.serverInfo = serverInfo;
            }
        }

        /**
         * Response to the server version request
         */
        @Getter
        public static class VersionExchange extends SakuyaBridgePacket {

            private int serverVersion;
            private int networkProtocol;

            public VersionExchange() {
            }

            public VersionExchange(int serverVersion, int networkProtocol) {
                this.serverVersion = serverVersion;
                this.networkProtocol = networkProtocol;
            }
        }

        /**
         * Represents basic login response
         */
        @Getter
        private static abstract class Login extends SakuyaBridgePacket {

            protected boolean success = false; // Default to false
            protected SessionToken sessionToken;

            public Login() {
            }

            public Login(boolean success, SessionToken sessionToken) {
                this.success = success;
                this.sessionToken = sessionToken;
            }
        }

        /**
         * Response to the previous session login request
         */
        @Getter
        public static class PreviousSessionLogin extends Login {

            public PreviousSessionLogin() {
            }

            public PreviousSessionLogin(boolean success, SessionToken sessionToken) {
                super(success, sessionToken);
            }
        }

        /**
         * Response to the username and password login request
         */
        @Getter
        public static class UsernamePasswordLogin extends Login {

            public UsernamePasswordLogin() {
            }

            public UsernamePasswordLogin(boolean success, SessionToken sessionToken) {
                super(success, sessionToken);
            }
        }

        /**
         * Response to the username and password register request
         */
        @Getter
        public static class UsernamePasswordRegister extends UsernamePasswordLogin {

            public UsernamePasswordRegister() {
            }

            public UsernamePasswordRegister(boolean success, SessionToken sessionToken) {
                super(success, sessionToken);
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
