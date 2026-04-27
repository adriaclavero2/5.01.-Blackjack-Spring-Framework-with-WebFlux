package com.blackjack.blackjack_api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_Returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found test");
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not found test", response.getBody().get("message"));
    }

    @Test
    void handleGameRuleException_Returns400() {
        GameRuleException ex = new GameRuleException("Rule broken test");
        ResponseEntity<Map<String, Object>> response = handler.handleGameRuleException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Rule broken test", response.getBody().get("message"));
    }

    @Test
    void handleResponseStatusException_ReturnsCorrectStatus() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "Conflict test");
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatusException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleGeneralException_Returns500() {
        Exception ex = new Exception("Internal error test");
        ResponseEntity<Map<String, Object>> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleInvalidEnumOrInput_Returns400WithMono() {
        ServerWebInputException ex = new ServerWebInputException("Bad input");
        Mono<Map<String, String>> responseMono = handler.handleInvalidEnumOrInput(ex);

        StepVerifier.create(responseMono)
                .expectNextMatches(map ->
                        map.get("error").equals("Bad Request") &&
                                map.containsKey("message"))
                .verifyComplete();
    }
}