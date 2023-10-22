package dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async listener manager for TimeStop
 */
@Getter
public class TimeStopListenerManager {

    private final ThreadPoolExecutor executor;
    private final List<TimeStopListener<?>> listeners = Collections.synchronizedList(new LinkedList<>());

    /**
     * Creates a new listener manager
     *
     * @param maxThreads The maximum amount of threads to use
     */
    public TimeStopListenerManager(int maxThreads) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * Registers a listener
     *
     * @param listener Listener to register
     */
    public void registerListener(TimeStopListener<?> listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a listener
     *
     * @param listener Listener to unregister
     */
    public void unregisterListener(TimeStopListener<?> listener) {
        listeners.remove(listener);
    }

    /**
     * Processes a received message
     *
     * @param connection Connection the message was received from
     * @param object     Message to process
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void process(Connection connection, Object object) {
        executor.execute(() -> {
            TimeStopListener.Context context = new TimeStopListener.Context(connection);

            listeners.stream()
                     .filter(listener -> listener.getListeningClass().isAssignableFrom(object.getClass()))
                     .sorted((listener1, listener2) -> Integer.compare(listener2.getPriority(), listener1.getPriority()))
                     .forEach(listenerWithParameter -> {
                         if (context.isShouldIgnore()) {
                             return;
                         }

                         // Cast to type with parameter
                         ((TimeStopListener) listenerWithParameter).process(context, listenerWithParameter.getListeningClass().cast(object));
                     });
        });
    }
}
