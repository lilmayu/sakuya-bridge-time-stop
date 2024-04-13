package dev.mayuna.sakuyabridge;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.commons.v2.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.v2.config.ConfigException;
import dev.mayuna.sakuyabridge.objects.TestConfigObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class TestConfig {

    private static final String configPath = "./config.json";
    private static final Gson gson = new Gson();

    @BeforeEach
    @AfterEach
    public void deleteConfig() {
        File configFile = new File(configPath);
        configFile.setReadable(true);
        configFile.setWritable(true);
        configFile.delete();
    }

    @Test
    public void saveConfig() {
        ApplicationConfigLoader.saveTo(gson, configPath, new TestConfigObject());

        File configFile = new File(configPath);
        assertTrue(configFile.exists());
    }

    @Test
    public void loadConfigWithDefaultValues() {
        TestConfigObject testConfig = ApplicationConfigLoader.loadFrom(gson, configPath, TestConfigObject.class, false);
        assertNotNull(testConfig);
    }

    @Test
    public void loadConfigThrowWhenNotExists() {
        assertThrows(ConfigException.class, () -> ApplicationConfigLoader.loadFrom(gson, configPath, TestConfigObject.class, true));
    }

    @Test
    public void loadConfig() {
        TestConfigObject testConfig = new TestConfigObject();
        testConfig.someNumber = 123;
        ApplicationConfigLoader.saveTo(gson, configPath, testConfig);

        TestConfigObject loadedTestConfig = ApplicationConfigLoader.loadFrom(gson, configPath, TestConfigObject.class, false);
        assertNotNull(loadedTestConfig);
        assertEquals(testConfig, loadedTestConfig);
    }

    @Test
    public void loadInvalidJson() {
        File configFile = new File(configPath);
        assertDoesNotThrow(() -> Files.writeString(configFile.toPath(), "invalid json"));
        assertThrows(ConfigException.class, () -> ApplicationConfigLoader.loadFrom(gson, configPath, TestConfigObject.class, false));
    }

    @Test
    public void testSaveToUnwritableFile() {
        File configFile = new File(configPath);
        assertDoesNotThrow(() -> Files.writeString(configFile.toPath(), "invalid json"));
        configFile.setWritable(false);
        assertThrows(ConfigException.class, () -> ApplicationConfigLoader.saveTo(gson, configPath, new TestConfigObject()));
    }

    @Test
    public void saveToInvalidPath() {
        assertThrows(ConfigException.class, () -> ApplicationConfigLoader.saveTo(gson, "", new TestConfigObject()));
    }

    @Test
    public void loadFromInvalidPath() {
        assertThrows(ConfigException.class, () -> ApplicationConfigLoader.loadFrom(gson, "", TestConfigObject.class, false));
    }
}
