package org.example.util;

import com.nimbusds.jose.jwk.RSAKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;

public class RSAKeyUtils {

    private RSAKeyUtils() {
    }

    public static String encodeRSAKeyToBase64(final byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

    public static byte[] decodeRSAKeyFromBase64(final String encodedKey) {
        return Base64.getDecoder().decode(encodedKey);
    }

    public static void saveEncodedRSAKeyToFile(final String path, final String encodedRsaKey) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(encodedRsaKey.getBytes());
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static String readEncodedRSAKeyFromFile(final String path) {
        try {
            return new String(Files.readAllBytes(new File(path).toPath()));
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    public static RSAKey rsaKeyGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    public static String rsaKeyToJson(RSAKey rsaKey) {
        return rsaKey.toJSONString();
    }
}
