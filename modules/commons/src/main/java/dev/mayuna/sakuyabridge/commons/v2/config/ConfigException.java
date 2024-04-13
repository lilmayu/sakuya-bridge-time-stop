package dev.mayuna.sakuyabridge.commons.v2.config;

public class ConfigException extends RuntimeException {

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(String message) {
        super(message);
    }
}
