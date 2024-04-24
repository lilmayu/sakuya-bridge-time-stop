package dev.mayuna.sakuyabridge.server.v2.managers.accounts;

import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.objects.accounts.UsernamePasswordAccount;
import lombok.NonNull;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UsernamePasswordAccountManager extends Pumpk1nAccountManager<UsernamePasswordAccount> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(UsernamePasswordAccountManager.class);
    private static final PasswordAuthentication PASSWORD_AUTHENTICATION = new PasswordAuthentication();

    public UsernamePasswordAccountManager(Pumpk1n pumpk1n) {
        super(LOGGER, pumpk1n);
    }

    /**
     * Creates a new account with the given username and password.
     *
     * @param username The username
     * @param password The password
     *
     * @return The account
     */
    public Optional<UsernamePasswordAccount> createAccount(@NonNull String username, char @NonNull [] password) {
        var account = tryCreateAccount(username);

        if (account.isEmpty()) {
            return Optional.empty();
        }

        account.get().setPasswordHash(PASSWORD_AUTHENTICATION.hash(password));

        return account;
    }

    @Override
    protected @NonNull UsernamePasswordAccount createAccount(@NonNull String username) {
        return new UsernamePasswordAccount(username);
    }

    @Override
    public boolean deleteAccount(@NonNull UUID uuid) {
        // TODO: Implement
        return false;
    }

    /**
     * Hash passwords for storage, and test passwords against password tokens.
     * <p>
     * Instances of this class can be used concurrently by multiple threads.
     *
     * @author erickson
     * @see <a href="http://stackoverflow.com/a/2861125/3474">StackOverflow</a>
     */
    public final static class PasswordAuthentication {

        /**
         * Each token produced by this class uses this identifier as a prefix.
         */
        public static final String ID = "$47$";

        /**
         * The minimum recommended cost, used by default
         */
        public static final int DEFAULT_COST = 16;

        private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

        private static final int SIZE = 128;

        private static final Pattern layout = Pattern.compile("\\$47\\$(\\d\\d?)\\$(.{43})");

        private final SecureRandom random;

        private final int cost;

        /**
         * Create a password manager with a specified cost
         */
        public PasswordAuthentication() {
            this(DEFAULT_COST);
        }

        /**
         * Create a password manager with a specified cost
         *
         * @param cost the exponential computational cost of hashing a password, 0 to 30
         */
        public PasswordAuthentication(int cost) {
            iterations(cost); /* Validate cost */
            this.cost = cost;
            this.random = new SecureRandom();
        }

        private static int iterations(int cost) {
            if ((cost < 0) || (cost > 30)) {
                throw new IllegalArgumentException("cost: " + cost);
            }
            return 1 << cost;
        }

        private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
            KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
            try {
                SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
                return f.generateSecret(spec).getEncoded();
            } catch (NoSuchAlgorithmException ex) {
                throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
            } catch (InvalidKeySpecException ex) {
                throw new IllegalStateException("Invalid SecretKeyFactory", ex);
            }
        }

        /**
         * Hash a password for storage.
         *
         * @return a secure authentication token to be stored for later authentication
         */
        public String hash(char[] password) {
            byte[] salt = new byte[SIZE / 8];
            random.nextBytes(salt);
            byte[] dk = pbkdf2(password, salt, 1 << cost);
            byte[] hash = new byte[salt.length + dk.length];
            System.arraycopy(salt, 0, hash, 0, salt.length);
            System.arraycopy(dk, 0, hash, salt.length, dk.length);
            Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
            return ID + cost + '$' + enc.encodeToString(hash);
        }

        /**
         * Authenticate with a password and a stored password token.
         *
         * @return true if the password and token match
         */
        public boolean authenticate(char[] password, String token) {
            Matcher m = layout.matcher(token);
            if (!m.matches()) {
                throw new IllegalArgumentException("Invalid token format");
            }
            int iterations = iterations(Integer.parseInt(m.group(1)));
            byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
            byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
            byte[] check = pbkdf2(password, salt, iterations);
            int zero = 0;
            for (int idx = 0; idx < check.length; ++idx) {
                zero |= hash[salt.length + idx] ^ check[idx];
            }
            return zero == 0;
        }
    }
}
