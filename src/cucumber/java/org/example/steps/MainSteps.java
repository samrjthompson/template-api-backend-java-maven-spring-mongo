package org.example.steps;

import static org.example.config.CucumberContext.CONTEXT;
import static org.example.config.WireMockTestConfig.getServeEvents;
import static org.example.config.WireMockTestConfig.getWireMockServer;
import static org.example.config.WireMockTestConfig.setupWireMock;
import static org.example.config.WireMockTestConfig.stubGet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class MainSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void beforeEachScenario() {
        CONTEXT.clear();
        setupWireMock();
    }

    @Given("the app is running")
    public void theAppIsRunning() {
        assertNotNull(testRestTemplate);
    }

    @And("a {int} is returned from the external app")
    public void stub(final int statusCode) {
        stubGet("/external-endpoint", statusCode);
    }

    @When("a GET request is sent to {string}")
    public void sendGetToController(final String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(endpoint, HttpMethod.GET, request, Void.class);
        CONTEXT.set("statusCode", response.getStatusCode().value());
    }

    @Then("a {int} response is returned")
    public void assertResponseCode(final int code) {
        assertEquals(code, (int) CONTEXT.get("statusCode"));
    }

    @And("a {string} request was made to the external server")
    public void verifyRequestWasMade(final String httpMethod) {
        RequestMethod requestmethod = switch (httpMethod) {
            case "GET" -> RequestMethod.GET;
            case "POST" -> RequestMethod.POST;
            case "PATCH" -> RequestMethod.PATCH;
            case "PUT" -> RequestMethod.PUT;
            case "DELETE" -> RequestMethod.DELETE;
            default -> throw new RuntimeException();
        };

        ServeEvent serveEvent = getServeEvents().getFirst();
        assertEquals("http://localhost:8888/external-endpoint", serveEvent.getRequest().getAbsoluteUrl());
        assertEquals(requestmethod, serveEvent.getRequest().getMethod());
    }
}
