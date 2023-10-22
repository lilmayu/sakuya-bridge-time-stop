package dev.mayuna.sakuyabridge.commons.config;

import com.google.gson.Gson;
import dev.mayuna.mayusjsonutils.JsonUtil;

import java.io.File;

public class ApplicationConfigLoader {

    /**
     * Loads the config from the given file path.
     *
     * @param gson                       Gson
     * @param filePath                   File path
     * @param clazz                      Config class
     * @param throwExceptionIfNotExisted Throw exception if config did not exist
     * @param <T>                        Config class type
     *
     * @return Config
     */
    public static <T> T loadFrom(Gson gson, String filePath, Class<T> clazz, boolean throwExceptionIfNotExisted) {
        File configFile = new File(filePath);

        if (!configFile.exists()) {
            try {
                JsonUtil.saveJson(gson.toJsonTree(clazz.getConstructor().newInstance()).getAsJsonObject(), configFile, true);
            } catch (Exception exception) {
                throw new ConfigException("Could not save default config " + clazz.getName() + " at " + configFile.getAbsolutePath() + "!", exception);
            }

            if (throwExceptionIfNotExisted) {
                throw new ConfigException("Config did not exist and default one was created. Please, edit it and start the application again.");
            }
        }

        T config;

        try {
            config = gson.fromJson(JsonUtil.loadJson(configFile.getAbsolutePath()).getJsonObject(), clazz);
        } catch (Exception exception) {
            throw new ConfigException("Could not load config " + clazz.getName() + " at " + configFile.getAbsolutePath() + "! Please, check if there are no errors in JSON.", exception);
        }

        try {
            JsonUtil.saveJson(gson.toJsonTree(config).getAsJsonObject(), configFile, true);
        } catch (Exception exception) {
            throw new ConfigException("Could not save loaded config " + clazz.getName() + " at " + configFile.getAbsolutePath() + "!", exception);
        }

        return config;
    }

    /**
     * Saves the config to the given file path.
     *
     * @param gson     Gson
     * @param filePath File path
     * @param config   Config
     */
    public static void saveTo(Gson gson, String filePath, Object config) {
        File configFile = new File(filePath);

        try {
            JsonUtil.saveJson(gson.toJsonTree(config).getAsJsonObject(), configFile, true);
        } catch (Exception exception) {
            throw new ConfigException("Could not save config " + config.getClass().getName() + " at " + configFile.getAbsolutePath() + "!", exception);
        }
    }
}
