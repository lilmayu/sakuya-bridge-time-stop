package dev.mayuna.sakuyabridge.commons.networking.tcp.timestop;

import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.TimeStopPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TimeStopPacketSegment {

    private UUID uuid;
    private UUID parentUuid;
    private byte[] data;
    private int segmentIndex;
    private int segmentCount;

    public TimeStopPacketSegment() {
        uuid = UUID.randomUUID();
    }

    public TimeStopPacketSegment(UUID parentUuid, byte[] data, int segmentIndex, int segmentCount) {
        this.data = data;
        this.parentUuid = parentUuid;
        this.segmentIndex = segmentIndex;
        this.segmentCount = segmentCount;
    }
}
