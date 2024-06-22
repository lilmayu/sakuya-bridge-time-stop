package dev.mayuna.sakuyabridge.server.v2;

import java.util.UUID;

public final class ServerConstants {

    public static final UUID MAIN_DATA_HOLDER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private ServerConstants() {
    }

    public static final class Responses {

        public static final String INTERNAL_SERVER_ERROR = "Internal server error";
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String USERNAME_ALREADY_TAKEN = "Username already taken";
        public static final String REGISTRATION_DISABLED = "Registration disabled";
        public static final String AUTH_METHOD_DISABLED = "Auth method disabled";
        public static final String INVALID_SESSION_TOKEN = "Invalid session token";
        public static final String CHAT_DISABLED = "Chat disabled";
        public static final String MESSAGE_TOO_LONG = "Message too long";
        public static final String UNKNOWN_CHAT_ROOM = "Unknown Chat Room";

        private Responses() {
        }
    }
}
