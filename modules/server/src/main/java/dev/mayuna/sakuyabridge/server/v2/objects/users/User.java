package dev.mayuna.sakuyabridge.server.v2.objects.users;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class User {

    private String username;
    private UUID uuid;

}
