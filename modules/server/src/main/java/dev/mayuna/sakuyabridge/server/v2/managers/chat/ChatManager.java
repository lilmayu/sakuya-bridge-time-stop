package dev.mayuna.sakuyabridge.server.v2.managers.chat;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatMessage;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.exceptions.ChatRoomAlreadyExistsException;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.chat.PublicServerChatRoomWrap;
import dev.mayuna.sakuyabridge.server.v2.objects.chat.ServerChatRoomWrap;
import dev.mayuna.sakuyabridge.server.v2.objects.games.Game;

import java.util.*;

/**
 * Chat Manager
 */
public final class ChatManager {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ChatManager.class);
    private static final Random RANDOM = new Random();

    private final Timer chatRoomDeleteTimer = new Timer();
    private final Config.ChatManager config;
    private final List<ServerChatRoomWrap> chatRooms = new LinkedList<>();

    /**
     * Creates new Chat Manager
     *
     * @param config {@link Config.ChatManager}
     */
    public ChatManager(Config.ChatManager config) {
        this.config = config;
    }

    /**
     * Initializes chat manager
     */
    public void init() {
        if (!isEnabled()) {
            LOGGER.warn("ChatManager is disabled");
            return;
        }

        LOGGER.info("Creating 'General' Chat Room...");
        synchronized (chatRooms) {
            chatRooms.add(new PublicServerChatRoomWrap(new ChatRoom("General")));
        }

        LOGGER.mdebug("Preparing ChatRoomDeleteTimer");
        createChatRoomDeleteTimerTask();

        LOGGER.success("ChatManager initialized");
    }

    /**
     * Creates ChatRoomDeleteTimer
     */
    private void createChatRoomDeleteTimerTask() {
        chatRoomDeleteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                deleteOldChatRooms();
            }
        }, 0, 10000);
    }

    /**
     * Determines if Chat Manager is enabled
     *
     * @return True if yes, false otherwise
     */
    public boolean isEnabled() {
        return config.isEnabled();
    }

    /**
     * Creates {@link ChatMessage}
     *
     * @param loggedAccount LoggedAccount
     * @param content       content
     *
     * @return {@link ChatMessage}
     */
    public ChatMessage createChatMessage(LoggedAccount loggedAccount, String content) {
        return new ChatMessage(loggedAccount, content);
    }

    /**
     * Sends message in specified Chat Room
     *
     * @param chatRoomWrap ServerChatRoomWrap
     * @param chatMessage  ChatMessage
     */
    public void sendMessage(ServerChatRoomWrap chatRoomWrap, ChatMessage chatMessage) {
        LOGGER.mdebug("[{}] Sent into '{}' message: '{}'", chatMessage.getAuthorAccount(),
                      chatRoomWrap.getChatRoom().getName(),
                      chatMessage.getContent()
        );
        chatRoomWrap.getChatRoom().addMessage(chatMessage);

        // Notify users
        List<SakuyaBridgeConnection> notifyToConnections;

        if (chatRoomWrap.isPublic()) {
            notifyToConnections = SakuyaBridge.INSTANCE.getServer().getConnectionsMapped();
        } else {
            notifyToConnections = SakuyaBridge.INSTANCE.getServer().getConnectionsByUsers(chatRoomWrap.getWhitelistedUsers());
        }

        notifyToConnections.forEach(connection -> {
            connection.sendTCP(new Packets.Notifications.Chat.ChatMessageSent(chatRoomWrap.getChatRoom().getName(), chatMessage));
        });
    }

    /**
     * Sends system message in specified Chat Room
     *
     * @param chatRoomWrap ServerChatRoomWrap
     * @param content      Message's content
     */
    public void sendSystemMessage(ServerChatRoomWrap chatRoomWrap, String content) {
        sendMessage(chatRoomWrap, createChatMessage(CommonConstants.SYSTEM_ACCOUNT, content));
    }

    /**
     * Removes specified user from the specified chat room
     *
     * @param chatRoomWrap ServerChatRoomWrap
     * @param user         User
     */
    public void leaveChatRoom(ServerChatRoomWrap chatRoomWrap, User user) {
        LOGGER.mdebug("[{}] left Chat Room '{}'", user.getLoggedAccount(), chatRoomWrap.getChatRoom().getName());
        chatRoomWrap.removeUserFromWhitelist(user);
    }

    /**
     * Returns {@link ServerChatRoomWrap} with the specified name
     *
     * @param name Chat Room name
     *
     * @return Optional of {@link ServerChatRoomWrap}
     */
    public Optional<ServerChatRoomWrap> getChatRoomByName(String name) {
        synchronized (chatRooms) {
            return chatRooms.stream().filter(chatRoom -> chatRoom.getChatRoom().getName().equals(name)).findFirst();
        }
    }

    /**
     * Returns list of {@link ServerChatRoomWrap} in which the specified {@link User} is whitelisted
     *
     * @param user User
     *
     * @return List of {@link ServerChatRoomWrap}
     */
    public List<ServerChatRoomWrap> getChatRoomsForUser(User user) {
        synchronized (chatRooms) {
            return chatRooms.stream().filter(chatRoom -> chatRoom.isUserWhitelisted(user)).toList();
        }
    }

    /**
     * Creates new Chat Room with specified name
     *
     * @param name                      Chat Room name
     * @param appendNumberWhenDuplicate Append number when duplicate
     *
     * @return {@link ServerChatRoomWrap}
     *
     * @throws ChatRoomAlreadyExistsException If there's already room with specified name
     */
    public ServerChatRoomWrap createChatRoom(String name, boolean appendNumberWhenDuplicate) {
        if (getChatRoomByName(name).isPresent()) {
            if (!appendNumberWhenDuplicate) {
                LOGGER.warn("Chat Room with name '{}' already exists", name);
                throw new ChatRoomAlreadyExistsException();
            }

            return createChatRoom(name + " (" + RANDOM.nextInt() + ")", true);
        }

        ChatRoom chatRoom = new ChatRoom(name);
        ServerChatRoomWrap serverChatRoomWrap = new ServerChatRoomWrap(chatRoom);

        synchronized (chatRooms) {
            chatRooms.add(serverChatRoomWrap);
        }

        LOGGER.mdebug("Created Chat Room with name '{}'", name);

        return serverChatRoomWrap;
    }

    /**
     * Creates new Chat Room for the {@link Game}
     *
     * @param game             Game
     * @param whitelistedUsers Whitelisted Users
     *
     * @return ServerChatRoomWrap
     */
    public ServerChatRoomWrap createChatRoom(Game game, List<User> whitelistedUsers) {
        ServerChatRoomWrap chatRoomWrap = createChatRoom(game.getGameInfo().getName() + " (Chat)", true);
        chatRoomWrap.addAllUuidsToWhitelist(whitelistedUsers);
        return chatRoomWrap;
    }

    /**
     * Deletes old chat rooms
     */
    public void deleteOldChatRooms() {
        synchronized (chatRooms) {
            chatRooms.removeIf(chatRoomWrap -> chatRoomWrap.shouldDelete(config.getDeleteGameChatRoomsAfterMillis()));
        }
    }
}
