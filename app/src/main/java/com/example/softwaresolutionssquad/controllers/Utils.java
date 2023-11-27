package com.example.softwaresolutionssquad.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for common operations needed across the application.
 */
public class Utils {

    /**
     * Generates a SHA-256 hash of the provided password.
     *
     * @param passwordToHash The password to be hashed.
     * @return A string representing the hashed password in hexadecimal format or {@code null} if the algorithm is not found.
     */
    public static String hashPassword(String passwordToHash) {
        try {
            // Create MessageDigest instance for SHA-256 encryption
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Perform the hash operation on input password
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();

            // Convert the hashed bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            // Return the hashed password in hex format
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Log the exception (ideally, this should use a logging framework)
            e.printStackTrace();
            return null;
        }
    }
}
