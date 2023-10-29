package dev.mayuna.sakuyabridge.server.users;

import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.Constants;
import dev.mayuna.sakuyabridge.server.Main;
import dev.mayuna.sakuyabridge.server.users.managers.DiscordUserManager;
import dev.mayuna.sakuyabridge.server.users.managers.UsernamePasswordUserManager;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserManagers {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(UserManagers.class);

    private StoredUsers storedUsers = new StoredUsers();

    private DiscordUserManager discordUserManager;
    private UsernamePasswordUserManager usernamePasswordUserManager;

    public UserManagers() {
    }

    /**
     * Loads stored users
     */
    public void loadStoredUsers() {
        LOGGER.info("Loading stored users...");
        storedUsers = Main.getPumpk1n().getOrCreateDataHolder(Constants.pumpk1nDataHolderIdUserManagers).getOrCreateDataElement(StoredUsers.class);
        storedUsers.synchronizedList();
        LOGGER.info("Loaded stored users: " + storedUsers.getUsers().size());
        LOGGER.info("Next ID: " + storedUsers.getNextId());
    }

    public void initUsernamePasswordUserManager() {
        usernamePasswordUserManager = new UsernamePasswordUserManager(storedUsers);
        LOGGER.mdebug("Initialized UsernamePasswordUserManager");
    }
}
