package dev.mayuna.sakuyabridge.commons.networking.tcp.base.serialization;

import com.esotericsoftware.kryo.Kryo;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.TimeStopPacket;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.TimeStopPacketSegment;

import java.util.UUID;

/**
 * Registers all classes that are used in the TimeStop protocol
 */
public class TimeStopSerialization {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(TimeStopSerialization.class);

    private TimeStopSerialization() {
    }

    /**
     * Registers all classes that are used in the TimeStop protocol
     *
     * @param kryo The Kryo instance to register the classes to
     */
    public static void register(Kryo kryo) {
        LOGGER.mdebug("Registering network classes...");
        long start = System.currentTimeMillis();

        registerJavaClasses(kryo);
        registerTimeStopClasses(kryo);

        LOGGER.mdebug("Registered network classes in {}ms", System.currentTimeMillis() - start);
    }

    /**
     * Registers all needed Java classes
     *
     * @param kryo The Kryo instance to register the classes to
     */
    private static void registerJavaClasses(Kryo kryo) {
        // Basic
        kryo.register(byte[].class);
        kryo.register(boolean.class);

        // Serializers
        kryo.register(UUID.class, new UUIDSerializer());
    }

    /**
     * Registers all needed TimeStop classes
     *
     * @param kryo The Kryo instance to register the classes to
     */
    private static void registerTimeStopClasses(Kryo kryo) {
        kryo.register(TimeStopPacket.class);
        kryo.register(TimeStopPacketSegment.class);

        // Packets
        kryo.register(Packets.BasePacket.class);
        kryo.register(Packets.ProtocolVersionExchange.class);
        kryo.register(Packets.AsymmetricKeyExchange.class);
        kryo.register(Packets.SymmetricKeyExchange.class);
        kryo.register(Packets.EncryptedCommunicationRequest.class);
    }
}
