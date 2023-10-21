package dev.mayuna.sakuyabridge.client;

import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;

public class Main {

    private static final SakuyaBridgeLogger logger = SakuyaBridgeLogger.create(Main.class);

    public static void main(String[] args) {
        logger.info("Sakuya Bridge: Time Stop - Client");
        logger.info("========================");
        logger.info("Made by: Mayuna");
        logger.info("== Information ==");
        logger.info("Version: " + Constants.VERSION);
        logger.info("Network Protocol Version: " + NetworkConstants.COMMUNICATION_PROTOCOL_VERSION);
        logger.info("=================");
        logger.info("");
    }

}
