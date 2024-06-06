package dev.mayuna.sakuyabridge.commons.v2.networking.udp;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds list of {@link UdpDestination}s with handful methods to check whenever destination is whitelisted (exists in the list)
 */
public final class UdpDestinationWhitelist {

    private final List<UdpDestination> whitelistedUdpDestionations = new LinkedList<>();

    /**
     * Creates new {@link UdpDestinationWhitelist}
     */
    public UdpDestinationWhitelist() {
    }

    /**
     * Determines if the specified UDP Destination is whitelisted
     *
     * @param udpDestination UDP Destination
     *
     * @return True if yes, false otherwise
     */
    public boolean isWhitelisted(UdpDestination udpDestination) {
        synchronized (whitelistedUdpDestionations) {
            return whitelistedUdpDestionations.stream().anyMatch(x -> x.equals(udpDestination));
        }
    }

    /**
     * Adds destination to the whitelist, if not already added
     *
     * @param udpDestination UDP Destionation
     */
    public void add(UdpDestination udpDestination) {
        if (isWhitelisted(udpDestination)) {
            return;
        }

        synchronized (whitelistedUdpDestionations) {
            whitelistedUdpDestionations.add(udpDestination);
        }
    }

    /**
     * Removes whitelisted destination by the specified UDP Destination
     *
     * @param udpDestination UDP Destination
     */
    public void remove(UdpDestination udpDestination) {
        synchronized (whitelistedUdpDestionations) {
            whitelistedUdpDestionations.removeIf(x -> x.equals(udpDestination));
        }
    }
}
