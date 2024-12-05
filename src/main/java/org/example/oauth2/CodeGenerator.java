package org.example.oauth2;

import static org.example.Main.NAMESPACE;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates code verifier and code challenge to be sent as part of the OAuth2 Authorize request.
 * This would be generated by the frontend application. But I'm doing it here to use for testing
 * purposes.
 */
public class CodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private CodeGenerator() {}

    public static void generate() throws NoSuchAlgorithmException {
        // VERIFIER
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] code = new byte[32];
//        secureRandom.nextBytes(code);
//
//        final String codeVerifier = Base64.getUrlEncoder()
//                .withoutPadding()
//                .encodeToString(code);

        String codeVerifier = "verifier";

        // CHALLENGE
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] digested = messageDigest.digest(codeVerifier.getBytes());
        String codeChallenge = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(digested);

        LOGGER.info("CODE VERIFIER: {}", codeVerifier);
        LOGGER.info("CODE CHALLENGE: {}", codeChallenge);
    }
}
