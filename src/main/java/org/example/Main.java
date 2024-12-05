package org.example;

import java.security.NoSuchAlgorithmException;
import org.example.oauth2.CodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static final String NAMESPACE = "my-application";

    public static void main(String[] args) throws NoSuchAlgorithmException {
        SpringApplication.run(Main.class, args);

        CodeGenerator.generate();
    }
}