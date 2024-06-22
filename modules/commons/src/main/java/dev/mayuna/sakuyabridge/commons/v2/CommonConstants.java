package dev.mayuna.sakuyabridge.commons.v2;

import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.AccountType;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;

import java.util.UUID;

public final class CommonConstants {

    public static final String OBJECTS_PACKAGE = "dev.mayuna.sakuyabridge.commons.v2.objects";

    public static final int DEFAULT_PORT = 28077;
    public static final int CURRENT_SERVER_VERSION = 1;
    public static final int CURRENT_CLIENT_VERSION = 1;
    public static final int CURRENT_NETWORK_PROTOCOL = 1;
    public static final int MINIMUM_USERNAME_LENGTH = 3;
    public static final int MAXIMUM_USERNAME_LENGTH = 24;
    public static final int MINIMUM_PASSWORD_LENGTH = 8;
    public static final int MAXIMUM_PASSWORD_LENGTH = 32;
    public static final int PING_INTERVAL = 5000;
    public static final int MAX_GAME_NAME_LENGTH = 64;

    public static final String SYSTEM_USERNAME = "SYSTEM";
    public static final String LOCAL_USERNAME = "LOCAL";
    public static final LoggedAccount SYSTEM_ACCOUNT = new LoggedAccount(SYSTEM_USERNAME, new UUID(0, 0), AccountType.ANONYMOUS);
    public static final LoggedAccount LOCAL_ACCOUNT = new LoggedAccount(LOCAL_USERNAME, new UUID(0, 0), AccountType.ANONYMOUS);
    public static final int MAX_MESSAGE_HISTORY_COUNT = 100;
    public static final int MAX_MESSAGE_LENGTH = 300;

    private CommonConstants() {
    }
}
