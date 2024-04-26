package dev.mayuna.sakuyabridge.v2;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.impl.FolderStorageHandler;
import dev.mayuna.sakuyabridge.server.v2.managers.accounts.Pumpk1nAccountManager;
import dev.mayuna.sakuyabridge.server.v2.managers.accounts.UsernamePasswordAccountManager;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UsernamePasswordAccountManagerTest {

    private final static String DATA_FOLDER = "./data/";

    private final static String USERNAME_1 = "mayuna";
    private final static String PASSWORD_1 = "password123";
    private final static String USERNAME_2 = "foobar";
    private final static String PASSWORD_2 = "password456";

    private UsernamePasswordAccountManager usernamePasswordAccountManager;

    /**
     * Cleans up the data folder
     */
    private static void cleanUp() {
        // Delete all files in the data folder
        File dataFolder = new File(DATA_FOLDER);
        if (dataFolder.exists()) {
            for (File file : dataFolder.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }

        // Delete the data folder
        dataFolder.delete();
    }

    @BeforeAll
    public static void setUpAll() {
        cleanUp();
    }

    @BeforeEach
    public void setUp() {
        cleanUp();
        usernamePasswordAccountManager = new UsernamePasswordAccountManager(Level.INFO, new Pumpk1n(new FolderStorageHandler(DATA_FOLDER)));
        usernamePasswordAccountManager.enablePumpk1nLogging();
        usernamePasswordAccountManager.enableAllPumpk1nLogs();
        usernamePasswordAccountManager.prepare();
    }

    @AfterEach
    public void teardown() {
        usernamePasswordAccountManager.shutdown();
        usernamePasswordAccountManager = null;
        cleanUp();
    }

    @Test
    public void authenticationTest() {
        var account1 = usernamePasswordAccountManager.createAccount(USERNAME_1, PASSWORD_1.toCharArray());
        var account2 = usernamePasswordAccountManager.createAccount(USERNAME_2, PASSWORD_2.toCharArray());

        assertTrue(account1.isPresent(), "Account 1 is not present - should be present");
        assertTrue(account2.isPresent(), "Account 2 is not present - should be present");

        assertTrue(usernamePasswordAccountManager.authenticate(USERNAME_1, PASSWORD_1.toCharArray()).isPresent(), "Account 1 could not be authenticated");
        assertTrue(usernamePasswordAccountManager.authenticate(USERNAME_2, PASSWORD_2.toCharArray()).isPresent(), "Account 1 could not be authenticated");

        assertFalse(usernamePasswordAccountManager.authenticate(USERNAME_1, PASSWORD_2.toCharArray()).isPresent(), "Account 1 could be authenticated with wrong password");
        assertFalse(usernamePasswordAccountManager.authenticate(USERNAME_2, PASSWORD_1.toCharArray()).isPresent(), "Account 2 could be authenticated with wrong password");
    }

    @Test
    public void loadTest() {
        var account1 = usernamePasswordAccountManager.createAccount(USERNAME_1, PASSWORD_1.toCharArray());
        var account2 = usernamePasswordAccountManager.createAccount(USERNAME_2, PASSWORD_2.toCharArray());

        assertTrue(account1.isPresent(), "Account 1 is not present - should be present");
        assertTrue(account2.isPresent(), "Account 2 is not present - should be present");

        String passwordHash1 = account1.get().getPasswordHash();

        usernamePasswordAccountManager.shutdown();

        assertTrue(Files.exists(Path.of(DATA_FOLDER, Pumpk1nAccountManager.MAIN_DATAHOLDER_UUID + ".json")), "Data holder file for account list does not exist");

        usernamePasswordAccountManager = new UsernamePasswordAccountManager(Level.INFO, new Pumpk1n(new FolderStorageHandler(DATA_FOLDER)));
        usernamePasswordAccountManager.enablePumpk1nLogging();
        usernamePasswordAccountManager.enableAllPumpk1nLogs();
        usernamePasswordAccountManager.prepare();

        assertTrue(usernamePasswordAccountManager.getAccountListDataElement()
                                                 .containsAccount(USERNAME_1), "Account 1 is not present in account list data element - should be present");
        assertTrue(usernamePasswordAccountManager.getAccountListDataElement()
                                                 .containsAccount(USERNAME_2), "Account 2 is not present in account list data element - should be present");

        String passwordHash2 = usernamePasswordAccountManager.getOrLoadAccount(USERNAME_1).get().getPasswordHash();

        assertEquals(passwordHash1, passwordHash2, "Password hash for account 1 is not the same after loading");

        assertTrue(usernamePasswordAccountManager.authenticate(USERNAME_1, PASSWORD_1.toCharArray()).isPresent(), "Account 1 could not be authenticated");
        assertTrue(usernamePasswordAccountManager.authenticate(USERNAME_2, PASSWORD_2.toCharArray()).isPresent(), "Account 1 could not be authenticated");

        assertFalse(usernamePasswordAccountManager.authenticate(USERNAME_1, PASSWORD_2.toCharArray()).isPresent(), "Account 1 could be authenticated with wrong password");
        assertFalse(usernamePasswordAccountManager.authenticate(USERNAME_2, PASSWORD_1.toCharArray()).isPresent(), "Account 2 could be authenticated with wrong password");
    }

    @Test
    public void deleteTest() {
        var account1 = usernamePasswordAccountManager.createAccount(USERNAME_1, PASSWORD_1.toCharArray());
        var account2 = usernamePasswordAccountManager.createAccount(USERNAME_2, PASSWORD_2.toCharArray());

        assertTrue(account1.isPresent(), "Account 1 is not present - should be present");
        assertTrue(account2.isPresent(), "Account 2 is not present - should be present");

        assertTrue(usernamePasswordAccountManager.getAccountListDataElement()
                                                 .containsAccount(USERNAME_1), "Account 1 is not present in account list data element - should be present");
        assertTrue(usernamePasswordAccountManager.getAccountListDataElement()
                                                 .containsAccount(USERNAME_2), "Account 2 is not present in account list data element - should be present");

        usernamePasswordAccountManager.deleteAccount(USERNAME_1);

        assertFalse(usernamePasswordAccountManager.getAccountListDataElement()
                                                  .containsAccount(USERNAME_1), "Account 1 is present in account list data element - should not be present");
        assertTrue(usernamePasswordAccountManager.getAccountListDataElement()
                                                 .containsAccount(USERNAME_2), "Account 2 is not present in account list data element - should be present");

        assertFalse(usernamePasswordAccountManager.authenticate(USERNAME_1, PASSWORD_1.toCharArray()).isPresent(), "Account 1 could be authenticated after deletion");
        assertTrue(usernamePasswordAccountManager.authenticate(USERNAME_2, PASSWORD_2.toCharArray()).isPresent(), "Account 2 could not be authenticated after deletion");
    }

    @Test
    public void createExistingUsernameTest() {
        var account1 = usernamePasswordAccountManager.createAccount(USERNAME_1, PASSWORD_1.toCharArray());
        assertTrue(account1.isPresent(), "Account 1 does not exist - should be present");

        var account2 = usernamePasswordAccountManager.createAccount(USERNAME_1, PASSWORD_2.toCharArray());
        assertFalse(account2.isPresent(), "Account 2 does exist, with the same username as account 1 - should not be the case (duplicate username)");
    }

    @Test
    public void authenticateNonExistingUsernameTest() {
        assertFalse(usernamePasswordAccountManager.authenticate("nonExistingUsername", PASSWORD_1.toCharArray()).isPresent(), "Non-existing account could be authenticated");
    }

    @Test
    public void deleteNonExistingAccountTest() {
        assertFalse(usernamePasswordAccountManager.deleteAccount("nonExistingUsername"), "Non-existing account could be deleted");
    }

    @Test
    public void createAccountWithInvalidDataTest() {
        assertThrows(IllegalArgumentException.class, () -> usernamePasswordAccountManager.createAccount("", PASSWORD_1.toCharArray()), "Account with empty username was created");
        assertThrows(IllegalArgumentException.class, () -> usernamePasswordAccountManager.createAccount(USERNAME_1, new char[0]), "Account with empty password was created");
    }
}
