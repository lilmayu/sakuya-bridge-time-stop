package dev.mayuna.sakuyabridge.client.v2.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResourcesUtils {

    /**
     * Gets the files in a directory in the resources.<br>
     * Throws a runtime exception if the directory does not exist or other errors occur.
     *
     * @param directoryPath The directory path
     *
     * @return The files in the directory
     */
    public static List<String> getResourceFiles(String directoryPath) {
        try {
            URI uri = ResourcesUtils.class.getClassLoader().getResource(directoryPath).toURI();
            Path myPath;

            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                myPath = fileSystem.getPath(directoryPath);
            } else {
                myPath = Paths.get(uri);
            }

            try (var filesWalk = Files.walk(myPath, 1)) {
                return filesWalk
                        .filter(Files::isRegularFile)
                        .map(myPath::relativize)
                        .map(Path::toString)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get resource files in directory: " + directoryPath, e);
        }
    }

    /**
     * Reads a resource file.<br>
     * Throws a runtime exception if the directory does not exist or other errors occur.
     *
     * @param filePath The file path
     *
     * @return The file content
     */
    public static String readResourceFile(String filePath) {
        try {
            return Files.readString(Paths.get(ResourcesUtils.class.getClassLoader().getResource(filePath).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
