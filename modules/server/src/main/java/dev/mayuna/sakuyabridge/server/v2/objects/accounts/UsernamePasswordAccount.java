package dev.mayuna.sakuyabridge.server.v2.objects.accounts;

import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public final class UsernamePasswordAccount extends Account {

    private String passwordHash;

    /**
     * Used for serialization.
     */
    public UsernamePasswordAccount() {
    }

    /**
     * Creates a new account with the given username.
     *
     * @param username The username
     */
    public UsernamePasswordAccount(String username) {
        super(username);
    }

    /**
     * Creates a new account with the given username and password hash.
     *
     * @param username     The username
     * @param passwordHash The password hash
     */
    public UsernamePasswordAccount(String username, String passwordHash) {
        super(username);
        this.passwordHash = passwordHash;
    }

    /**
     * Creates a new account with the given username, password hash and UUID.
     *
     * @param username     The username
     * @param passwordHash The password hash
     * @param uuid         The UUID
     */
    public UsernamePasswordAccount(String username, UUID uuid, String passwordHash) {
        super(username, uuid);
        this.passwordHash = passwordHash;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.USERNAME_PASSWORD;
    }
}
