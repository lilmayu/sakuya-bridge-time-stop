package dev.mayuna.sakuyabridge.server.v1.users.objects;

import dev.mayuna.discord.oauth.entities.DiscordAccessToken;
import dev.mayuna.pumpk1n.api.DataElement;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DiscordUser extends User implements DataElement {

    private transient DiscordAccessToken accessToken;
    private dev.mayuna.discord.api.entities.DiscordUser discordUser;

    /**
     * For serialization
     */
    public DiscordUser() {
    }

    /**
     * Creates a new {@link DiscordUser} with the given {@link DiscordAccessToken} and {@link dev.mayuna.discord.api.entities.DiscordUser}
     *
     * @param accessToken The {@link DiscordAccessToken} to use
     * @param discordUser The {@link dev.mayuna.discord.api.entities.DiscordUser} to use
     * @param id          The ID to use
     */
    public DiscordUser(DiscordAccessToken accessToken, dev.mayuna.discord.api.entities.DiscordUser discordUser, int id) {
        this.accessToken = accessToken;
        this.discordUser = discordUser;
        this.id = id;
    }

    @Override
    @NonNull String getUsername() {
        return discordUser.getUsername();
    }
}
