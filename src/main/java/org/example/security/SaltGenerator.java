package org.example.security;

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

public class SaltGenerator implements StringKeyGenerator {

    @Override
    public String generateKey() {
        return KeyGenerators.string().generateKey();
    }
}
