package dev.mayuna.sakuyabridge.commons.v2.networking;

import com.esotericsoftware.kryo.Kryo;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.timestop.networking.base.serialization.UUIDSerializer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public final class NetworkRegistration {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Packets.class);

    /**
     * Registers the network classes
     *
     * @param kryo Kryo instance
     */
    public static void register(Kryo kryo) {
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(UUID.class);
        kryo.register(char[].class);

        registerClassesRecursively(Packets.class, kryo);
        registerClassesInPackage("dev.mayuna.sakuyabridge.commons.v2.objects", kryo);
    }

    /**
     * Registers a class
     *
     * @param clazz The class
     * @param kryo  The Kryo instance
     *
     * @return Whenever should be allowed for registering inner classes
     */
    private static boolean registerClass(Class<?> clazz, Kryo kryo) {
        if (shouldIgnoreClass(clazz)) {
            // Commented out
            //LOGGER.flow("Ignoring network class: " + clazz.getName());

            return !shouldIgnoreInnerClasses(clazz); // Can register inner classes
        }

        LOGGER.flow("Registering network class: " + clazz.getName());
        kryo.register(clazz);
        return true;
    }

    /**
     * Registers all inner classes recursively
     *
     * @param clazz The class
     * @param kryo  The Kryo instance
     */
    private static void registerClassesRecursively(Class<?> clazz, Kryo kryo) {
        if (!registerClass(clazz, kryo)) {
            return;
        }

        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            if (registerClass(innerClass, kryo)) {
                registerClassesRecursively(innerClass, kryo);
            }
        }
    }

    /**
     * Registers all classes in a package
     *
     * @param packageName The package name
     * @param kryo        The Kryo instance
     */
    private static void registerClassesInPackage(String packageName, Kryo kryo) {
        LOGGER.flow("Registering network classes in package: " + packageName);
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        Set<Class<? extends Enum>> enums = reflections.getSubTypesOf(Enum.class);

        for (Class<?> clazz : classes) {
            registerClassesRecursively(clazz, kryo);
        }

        for (Class<? extends Enum> enumClass : enums) {
            registerClassesRecursively(enumClass, kryo);
        }
    }

    /**
     * Checks if a class should be ignored
     *
     * @param clazz The class
     *
     * @return Whether the class should be ignored
     */
    private static boolean shouldIgnoreClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(IgnoreNetworkRegistration.class);
    }

    /**
     * Checks if inner classes should be ignored
     *
     * @param clazz The class
     *
     * @return Whether inner classes should be ignored
     */
    private static boolean shouldIgnoreInnerClasses(Class<?> clazz) {
        IgnoreNetworkRegistration ignoreNetworkRegistration = clazz.getAnnotation(IgnoreNetworkRegistration.class);

        if (ignoreNetworkRegistration == null) {
            return false;
        }

        return ignoreNetworkRegistration.ignoreInnerClasses();
    }
}
