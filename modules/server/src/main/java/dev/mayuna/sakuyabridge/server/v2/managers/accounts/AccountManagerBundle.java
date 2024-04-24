package dev.mayuna.sakuyabridge.server.v2.managers.accounts;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import lombok.Getter;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public final class AccountManagerBundle {

    public static final Level LOG_LEVEL = SakuyaBridgeLogger.MDEBUG;

    private final UsernamePasswordAccountManager usernamePassword = new UsernamePasswordAccountManager();

    private final List<AccountManager<?>> accountManagers = List.of(usernamePassword);

    /**
     * Gets the account with the given UUID.
     *
     * @param uuid The UUID
     *
     * @return Optional of account
     */
    public Optional<Account> getAccount(UUID uuid) {
        return Optional.ofNullable(accountManagers.stream()
                                                  .map(manager -> manager.getAccount(uuid))
                                                  .filter(Optional::isPresent)
                                                  .map(Optional::get)
                                                  .findFirst().orElse(null));
    }

    /**
     * Gets the account with the given username.
     *
     * @param username The username
     *
     * @return Optional of account
     */
    public Optional<Account> getAccount(String username) {
        return Optional.ofNullable(accountManagers.stream()
                                                  .map(manager -> manager.getAccount(username))
                                                  .filter(Optional::isPresent)
                                                  .map(Optional::get)
                                                  .findFirst().orElse(null));
    }
}
