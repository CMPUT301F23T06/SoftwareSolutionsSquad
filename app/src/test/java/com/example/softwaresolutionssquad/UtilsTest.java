package com.example.softwaresolutionssquad;

import com.example.softwaresolutionssquad.controllers.Utils;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testHashPassword() {
        // Test with a known input and output
        String password = "TestPassword";
        String expectedHash = "7bcf9d89298f1bfae16fa02ed6b61908fd2fa8de45dd8e2153a3c47300765328"; // Expected SHA-256 hash for "TestPassword"

        String hashedPassword = Utils.hashPassword(password);

        assertEquals(expectedHash, hashedPassword);
    }

    @Test
    public void testHashPasswordWithEmptyString() {
        // Test with an empty string
        String password = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // Expected SHA-256 hash for an empty string

        String hashedPassword = Utils.hashPassword(password);

        assertEquals(expectedHash, hashedPassword);
    }
}
