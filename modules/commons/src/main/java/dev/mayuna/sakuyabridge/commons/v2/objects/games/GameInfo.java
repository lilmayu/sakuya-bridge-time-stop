package dev.mayuna.sakuyabridge.commons.v2.objects.games;

import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents information about a game
 */
@Getter @Setter
public final class GameInfo {

    private String name;
    private Region region;
    private Status status;
    private PlayerSide preferredPlayerSide;
    private Version version;
    private Technology technology;
    private User createdByUser;
    private boolean privateGame;
    private boolean passwordProtected;
    private boolean dynamicPingStrategyEnabled = true;
    private boolean udpHolePunchingEnabled = true;

    /**
     * Used for serialization
     */
    public GameInfo() {
    }

    /**
     * Represents a region where a game is hosted
     */
    public enum Region {
        US_EAST,
        US_WEST,
        CANADA,
        EUROPE,
        ASIA,
        OCEANIA,
        OTHER;
    }

    /**
     * Represents the status of a game
     */
    public enum Status {
        STARTING,
        WAITING,
        PLAYING;
    }

    /**
     * Represents the side of a player
     */
    public enum PlayerSide {
        RANDOM,
        LEFT,
        RIGHT;
    }

    /**
     * Represents the version of a game
     */
    public enum Version {
        TOUHOU_9,
        TOUHOU_19;
    }

    /**
     * Represents the technology used by a game
     */
    @Getter
    public enum Technology {
        ADONIS(Version.TOUHOU_9),
        ADONISE(Version.TOUHOU_9),
        ADONIS_2(Version.TOUHOU_9),
        ADONIS_2E(Version.TOUHOU_9);

        private final Version compatibleGame;

        Technology(Version compatibleGame) {
            this.compatibleGame = compatibleGame;
        }
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "name='" + name + '\'' +
                ", region=" + region +
                ", status=" + status +
                ", preferredPlayerSide=" + preferredPlayerSide +
                ", version=" + version +
                ", technology=" + technology +
                ", createdByUser=" + createdByUser +
                ", privateGame=" + privateGame +
                ", passwordProtected=" + passwordProtected +
                ", udpHolePunchingEnabled=" + udpHolePunchingEnabled +
                '}';
    }
}
