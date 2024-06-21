package dev.mayuna.sakuyabridge.commons.v2.objects.chat;

import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import lombok.Getter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents basic chat message with author username and its content
 */
@Getter
public class ChatMessage {

    /**
     * Date time format for {@link #sentOnMillisUtc} of <code>dd. MM. HH:mm</code>
     */
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("'['dd. MM. yyyy']' HH:mm");

    private LoggedAccount authorAccount;
    private String content;
    private long sentOnMillisUtc;

    private transient ZonedDateTime zonedDateTimeCache;

    /**
     * Used in serialization
     */
    public ChatMessage() {
    }

    /**
     * Creates new {@link ChatMessage}
     *
     * @param authorAccount   Author's account
     * @param content         Message's content
     * @param sentOnMillisUtc Message's sent time UTC
     */
    public ChatMessage(LoggedAccount authorAccount, String content, long sentOnMillisUtc) {
        this.authorAccount = authorAccount;
        this.content = content;
        this.sentOnMillisUtc = sentOnMillisUtc;
    }

    /**
     * Creates new {@link ChatMessage} with system's UTC time
     *
     * @param authorAccount Author's account
     * @param content       Message's content
     */
    public ChatMessage(LoggedAccount authorAccount, String content) {
        this.authorAccount = authorAccount;
        this.content = content;
        this.sentOnMillisUtc = getSystemUtcMillis();
    }

    /**
     * Returns time in millis from system UTC
     *
     * @return Millis
     */
    private static long getSystemUtcMillis() {
        return Instant.now(Clock.systemUTC()).toEpochMilli();
    }

    /**
     * Returns {@link ZonedDateTime} based from {@link #sentOnMillisUtc} with system's default timezone
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getSentOnWithSystemTimezone() {
        if (zonedDateTimeCache != null) {
            return zonedDateTimeCache;
        }

        zonedDateTimeCache = Instant.ofEpochMilli(sentOnMillisUtc).atZone(ZoneId.systemDefault());
        return zonedDateTimeCache;
    }

    /**
     * Returns the {@link #sentOnMillisUtc} with system's default timezone formatted as {@link #DATE_TIME_FORMAT}
     *
     * @return String
     */
    public String getSentOnString() {
        return getSentOnWithSystemTimezone().format(DATE_TIME_FORMAT);
    }
}
