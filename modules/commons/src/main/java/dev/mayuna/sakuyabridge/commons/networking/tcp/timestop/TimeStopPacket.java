package dev.mayuna.sakuyabridge.commons.networking.tcp.timestop;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TimeStopPacket {

    private UUID uuid;
    private byte[] data;

    public TimeStopPacket() {
        uuid = UUID.randomUUID();
    }

    public TimeStopPacket(byte[] data) {
        this();
        this.data = data;
    }
}
