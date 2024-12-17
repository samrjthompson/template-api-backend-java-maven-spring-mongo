package org.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
public class Controller {

    private final WebClient webClient;

    public Controller(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthcheck() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetch/external")
    public ResponseEntity<String> fetchExternal() {
        HttpStatusCode statusCode;
        ResponseEntity<String> responseEntity = ResponseEntity.ok().build();
        try {
            responseEntity = webClient.get()
                    .uri("/external-endpoint")
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            statusCode = responseEntity.getStatusCode();
        } catch (WebClientResponseException ex) {
            statusCode = ex.getStatusCode();
        }
        return ResponseEntity.status(statusCode).build();
    }
}
