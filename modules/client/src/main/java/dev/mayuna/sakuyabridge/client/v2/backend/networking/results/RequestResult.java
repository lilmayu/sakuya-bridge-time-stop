package dev.mayuna.sakuyabridge.client.v2.backend.networking.results;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the result of a request
 *
 * @param <T> The type of the result (enum, object, etc.)
 */
@Getter
public final class RequestResult<T> {

    private final T result;
    private final boolean successful;
    private final String errorMessage;
    private @Setter boolean timedOut;

    /**
     * Creates a new request result
     *
     * @param result       The result
     * @param successful   Whether the request was successful
     * @param errorMessage The error message
     */
    private RequestResult(T result, boolean successful, String errorMessage) {
        this.result = result;
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a successful request result
     *
     * @param result The result
     * @param <T>    The type of the result
     *
     * @return The request result
     */
    public static <T> RequestResult<T> success(T result) {
        return new RequestResult<>(result, true, null);
    }

    /**
     * Creates a failed request result
     *
     * @param errorMessage The error message
     * @param <T>          The type of the result
     *
     * @return The request result
     */
    public static <T> RequestResult<T> failure(String errorMessage) {
        return new RequestResult<>(null, false, errorMessage);
    }

    /**
     * Creates a failed request result
     *
     * @param result       The result
     * @param errorMessage The error message
     * @param <T>          The type of the result
     *
     * @return The request result
     */
    public static <T> RequestResult<T> failure(T result, String errorMessage) {
        return new RequestResult<>(result, false, errorMessage);
    }

    /**
     * Creates a timed out request result
     *
     * @param <T> The type of the result
     *
     * @return The request result
     */
    public static <T> RequestResult<T> timeout() {
        RequestResult<T> result = new RequestResult<>(null, false, "Request timed out");
        result.setTimedOut(true);
        return result;
    }
}
