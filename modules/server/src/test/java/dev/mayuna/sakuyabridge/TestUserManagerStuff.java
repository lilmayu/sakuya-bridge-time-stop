package dev.mayuna.sakuyabridge;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.impl.FolderStorageHandler;
import dev.mayuna.pumpk1n.objects.DataHolder;
import dev.mayuna.sakuyabridge.server.v1.Main;
import dev.mayuna.sakuyabridge.server.v1.users.UserManagers;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserManagerStuff {

    private static final String dataDirectory = "./data/";
    private static UserManagers userManagers = new UserManagers();

    @AfterEach
    @BeforeEach
    public void cleanupAfterEach() {
        if (userManagers != null) {
            userManagers.getStoredUsers().getUsers().clear();
        }

        cleanup();

        userManagers = new UserManagers();
        Main.setPumpk1n(new Pumpk1n(new FolderStorageHandler(dataDirectory)));
        Main.getPumpk1n().prepareStorage();
        userManagers.loadStoredUsers();
        userManagers.initUsernamePasswordUserManager();
    }

    private void cleanup() {
        File dataFolder = new File(dataDirectory);

        if (!dataFolder.exists()) {
            return;
        }

        // Delete all files in the data folder
        for (File file : dataFolder.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }

        // Delete the data folder
        dataFolder.delete();
    }

    @Test
    public void testCreateUserAndAuthenticate() {
        String username = "mayuna_";
        String password = "password123";

        var createdUser = userManagers.getUsernamePasswordUserManager().createUser(username, password);
        assertNotNull(createdUser);
        assertTrue(userManagers.getUsernamePasswordUserManager().authenticateUser(username, password));
        assertFalse(userManagers.getUsernamePasswordUserManager().authenticateUser(username, "wrongpassword"));
    }

    @Test
    public void testCreateUserTwice() {
        String username = "mayuna_";
        String password = "password123";

        var createdUser = userManagers.getUsernamePasswordUserManager().createUser(username, password);
        assertNotNull(createdUser);
        assertThrows(IllegalArgumentException.class, () -> userManagers.getUsernamePasswordUserManager().createUser(username, password));
    }

    @Test
    public void testAuthenticateNonexistentUser() {
        String username = "mayuna_";
        String password = "password123";

        assertThrows(IllegalArgumentException.class, () -> userManagers.getUsernamePasswordUserManager().authenticateUser(username, password));
    }

    @Test
    public void testUserManagersBetweenRuns() {
        String username = "mayuna_";
        String password = "password123";

        var createdUser = userManagers.getUsernamePasswordUserManager().createUser(username, password);
        assertNotNull(createdUser);
        assertTrue(userManagers.getUsernamePasswordUserManager().authenticateUser(username, password));
        assertFalse(userManagers.getUsernamePasswordUserManager().authenticateUser(username, "wrongpassword"));

        for (DataHolder dataHolder : Main.getPumpk1n().getDataHolderList()) {
            dataHolder.save();
        }

        userManagers = new UserManagers();
        Main.setPumpk1n(new Pumpk1n(new FolderStorageHandler(dataDirectory)));
        userManagers.loadStoredUsers();
        userManagers.initUsernamePasswordUserManager();

        assertTrue(userManagers.getUsernamePasswordUserManager().authenticateUser(username, password));
        assertFalse(userManagers.getUsernamePasswordUserManager().authenticateUser(username, "wrongpassword"));
    }
}
