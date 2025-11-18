package com.example.herculean.security;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class Encryption {

    // Generates a random 16 byte salt for hashing
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hashes a password with a given salt using SHA-256
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            Log.e("encryption", "SHA-256 algo not found", e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.e("encryption", "Salt is not valid Base64", e);
            return null;
        }
    }
}
