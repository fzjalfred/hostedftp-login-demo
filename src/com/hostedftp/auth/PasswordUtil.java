package com.hostedftp.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LEN = 16;    // bytes

    public static String[] hashPassword(char[] password) {
        byte[] salt = new byte[SALT_LEN];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);
        return new String[]{
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash),
                String.valueOf(ITERATIONS),
                "PBKDF2WithHmacSHA256"
        };
    }

    public static boolean verifyPassword(char[] password, String b64Salt, String b64Hash, int iterations, String alg) {
        byte[] salt = Base64.getDecoder().decode(b64Salt);
        byte[] expected = Base64.getDecoder().decode(b64Hash);
        byte[] actual = pbkdf2(password, salt, iterations, expected.length * 8, alg);
        if (actual.length != expected.length) return false;
        int diff = 0;
        for (int i = 0; i < actual.length; i++) diff |= actual[i] ^ expected[i];
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBits) {
        return pbkdf2(password, salt, iterations, keyLenBits, "PBKDF2WithHmacSHA256");
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBits, String alg) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(alg);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}