package dev.mayuna.sakuyabridge.commons.v2.objects.chat;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;

/**
 * System message with {@link CommonConstants#SYSTEM_ACCOUNT} account
 */
public final class SystemChatMessage extends ChatMessage {

    /**
     * Creates new {@link SystemChatMessage}
     *
     * @param content         Message's content
     * @param sentOnMillisUtc Message's sent time UTC
     */
    public SystemChatMessage(String content, long sentOnMillisUtc) {
        super(CommonConstants.SYSTEM_ACCOUNT, content, sentOnMillisUtc);
    }

    /**
     * Creates new {@link SystemChatMessage}
     *
     * @param content Message's content
     */
    public SystemChatMessage(String content) {
        super(CommonConstants.SYSTEM_ACCOUNT, content);
    }
}
