package dev.mayuna.sakuyabridge.client.v2.frontend.lang;

import dev.mayuna.sakuyabridge.client.v2.frontend.util.StringUtils;
import dev.mayuna.sakuyabridge.client.v2.util.ResourcesUtils;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the language packs.
 */
@Getter
public final class LanguageManager {

    public static final String DEFAULT_LANGUAGE_PACK_ID = "en_US";
    public static final String LANGUAGE_PACKS_RESOURCES_DIRECTORY = "lang/";
    public static final String LANGUAGE_PACKS_FILE_SYSTEM_DIRECTORY = "./lang/";

    public static final LanguageManager INSTANCE = new LanguageManager();

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(LanguageManager.class);

    private final List<LanguagePack> availableLanguagePacks = new ArrayList<>();

    private LanguagePack currentLanguagePack;
    private LanguagePack fallbackLanguagePack;

    /**
     * If true, the language packs will be ignored and the translations will be returned as the keys.
     */
    private @Setter boolean ignoreLanguagePacks = false;

    private LanguageManager() {
    }

    /**
     * Gets a translation from the current language pack.
     *
     * @param key The key of the translation.
     *
     * @return The translation.
     */
    public String getTranslation(String key) {
        if (ignoreLanguagePacks) {
            return key;
        }

        if (currentLanguagePack != null) {
            var translation = currentLanguagePack.getTranslation(key);

            if (translation != null) {
                return translation;
            }
        }

        if (fallbackLanguagePack != null) {
            var translation = fallbackLanguagePack.getTranslation(key);

            if (translation != null) {
                return translation;
            }
        }

        LOGGER.warn("Translation not found: " + key);
        return key;
    }

    /**
     * Formats a translation from the current language pack with arguments.
     *
     * @param key  The key of the translation.
     * @param args The arguments to format the translation with.
     *
     * @return The formatted translation.
     */
    public String formatTranslation(String key, Object... args) {
        if (ignoreLanguagePacks) {
            return key;
        }

        if (currentLanguagePack != null) {
            var translation = currentLanguagePack.formatTranslation(key, args);

            if (translation != null) {
                return translation;
            }
        }

        if (fallbackLanguagePack != null) {
            var translation = fallbackLanguagePack.formatTranslation(key, args);

            if (translation != null) {
                return translation;
            }
        }

        LOGGER.warn("Translation not found: " + key);
        return key;
    }

    /**
     * Loads the language packs.<br>
     * If there won't be current or fallback language pack, returns false.
     *
     * @return True if the language packs were loaded successfully.
     */
    public boolean loadLanguagePacks() {
        LOGGER.info("Loading language packs");

        clearLanguagePacks();

        loadLanguagePacksFromResources();
        loadLanguagePacksFromFileSystem();

        LOGGER.info("Loaded " + availableLanguagePacks.size() + " language packs");

        setLanguagePack(DEFAULT_LANGUAGE_PACK_ID);
        setFallbackLanguagePack(DEFAULT_LANGUAGE_PACK_ID);

        if (!isEverythingOkay()) {
            LOGGER.error("There was an error loading the language packs (current language pack: " + currentLanguagePack + " (should be " + DEFAULT_LANGUAGE_PACK_ID + "), fallback language pack: " + fallbackLanguagePack + " (should be " + DEFAULT_LANGUAGE_PACK_ID + ")");
            return false;
        }

        return true;
    }

    /**
     * Checks if everything is okay.
     *
     * @return True if everything is okay.
     */
    public boolean isEverythingOkay() {
        return currentLanguagePack != null && fallbackLanguagePack != null;
    }

    /**
     * Gets a language pack by ID.
     *
     * @param languagePackId The language pack ID.
     *
     * @return The language pack, or null if not found.
     */
    public LanguagePack getLanguagePack(String languagePackId) {
        synchronized (availableLanguagePacks) {
            return availableLanguagePacks.stream()
                                         .filter(x -> x.getId().equals(languagePackId))
                                         .findFirst()
                                         .orElse(null);
        }
    }

    /**
     * Sets the current language pack.
     *
     * @param languagePack The language pack to set.
     */
    public void setLanguagePack(@NonNull LanguagePack languagePack) {
        LOGGER.info("Setting language pack: " + languagePack);
        currentLanguagePack = languagePack;
    }

    /**
     * Sets the fallback language pack.
     *
     * @param languagePack The language pack to set.
     */
    public void setFallbackLanguagePack(@NonNull LanguagePack languagePack) {
        LOGGER.info("Setting fallback language pack: " + languagePack);
        fallbackLanguagePack = languagePack;
    }

    /**
     * Sets the current language pack.
     *
     * @param languagePackId The language pack ID to set.
     */
    public void setLanguagePack(String languagePackId) {
        synchronized (availableLanguagePacks) {
            var languagePack = getLanguagePack(languagePackId);

            if (languagePack == null) {
                LOGGER.warn("Language pack ID not found: " + languagePackId);
                return;
            }

            setLanguagePack(languagePack);
        }
    }

    /**
     * Sets the fallback language pack.
     *
     * @param languagePackId The language pack ID to set.
     */
    public void setFallbackLanguagePack(String languagePackId) {
        synchronized (availableLanguagePacks) {
            var languagePack = getLanguagePack(languagePackId);

            if (languagePack == null) {
                LOGGER.error("Fallback language pack ID not found: " + languagePackId);
                return;
            }

            setFallbackLanguagePack(languagePack);
        }
    }

    private void clearLanguagePacks() {
        synchronized (availableLanguagePacks) {
            availableLanguagePacks.clear();
        }
    }

    /**
     * Sets the current language pack.
     *
     * @param languagePack The language pack to set.
     */
    private void addOrReplaceLanguagePack(LanguagePack languagePack) {
        synchronized (availableLanguagePacks) {
            availableLanguagePacks.removeIf(pack -> {
                if (pack.getId().equals(languagePack.getId())) {
                    LOGGER.info("Removing old language pack: " + pack);
                    return true;
                }

                return false;
            });

            LOGGER.info("Adding language pack: " + languagePack);
            availableLanguagePacks.add(languagePack);
        }
    }

    /**
     * Loads the language packs from the resources.
     */
    private void loadLanguagePacksFromResources() {
        LOGGER.info("Loading language packs from resources");

        // Get the resource files
        List<String> languagePackFiles;

        try {
            languagePackFiles = ResourcesUtils.getResourceFiles(LANGUAGE_PACKS_RESOURCES_DIRECTORY);
        } catch (Exception exception) {
            LOGGER.error("Failed to load language packs from resources", exception);
            return;
        }

        // Load the language packs
        for (String languageFile : languagePackFiles) {
            final String path = LANGUAGE_PACKS_RESOURCES_DIRECTORY + languageFile;
            String json;

            try {
                json = ResourcesUtils.readResourceFile(path);
            } catch (Exception exception) {
                LOGGER.error("Failed to load language pack from resource: " + path, exception);
                continue;
            }

            LanguagePack languagePack;

            try {
                languagePack = LanguagePack.loadFromJson(json);
            } catch (Exception exception) {
                LOGGER.error("Failed to load language pack (" + path + ") from JSON: " + json, exception);
                continue;
            }

            addOrReplaceLanguagePack(languagePack);
        }
    }

    /**
     * Loads the language packs from the file system.
     */
    private void loadLanguagePacksFromFileSystem() {
        LOGGER.info("Loading language packs from file system");

        final Path languagePacksFileSystemDirectoryPath = Path.of(LANGUAGE_PACKS_FILE_SYSTEM_DIRECTORY);

        // Check if the directory exists
        if (!Files.exists(languagePacksFileSystemDirectoryPath)) {
            LOGGER.info("Language packs directory does not exist: " + LANGUAGE_PACKS_FILE_SYSTEM_DIRECTORY);
            return;
        }

        // Get the files in the directory
        final List<String> languagePackFiles = new ArrayList<>();

        try (var fileList = Files.list(languagePacksFileSystemDirectoryPath)) {
            fileList.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(File::getName)
                    .forEach(languagePackFiles::add);
        } catch (Exception exception) {
            LOGGER.error("Failed to list language pack files in file system directory: " + LANGUAGE_PACKS_FILE_SYSTEM_DIRECTORY, exception);
            return;
        }

        // Load the language packs
        for (String languageFile : languagePackFiles) {
            final String path = LANGUAGE_PACKS_FILE_SYSTEM_DIRECTORY + languageFile;
            String json;

            try {
                json = Files.readAllLines(Path.of(path)).stream().reduce("", String::concat);
            } catch (Exception exception) {
                LOGGER.error("Failed to load language pack from file system: " + path, exception);
                continue;
            }

            LanguagePack languagePack;

            try {
                languagePack = LanguagePack.loadFromJson(json);
            } catch (Exception exception) {
                LOGGER.error("Failed to load language pack (" + path + ") from JSON: " + json, exception);
                continue;
            }

            addOrReplaceLanguagePack(languagePack);
        }
    }
}
