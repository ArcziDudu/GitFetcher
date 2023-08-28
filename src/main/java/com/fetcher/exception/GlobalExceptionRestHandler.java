package com.fetcher.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionRestHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    protected ResponseEntity<ErrorResponse> handleNotFound(WebClientResponseException.NotFound ex) {
        String username = extractUsernameFromPath(Objects.requireNonNull(ex.getRequest()).getURI().getPath());
        log.error("User [{}] not exists in GitHub", username);
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(ex.getStatusCode().value(), "user ["+username+"] not found"));
    }
    @ExceptionHandler(badHeaderException.class)
    protected ResponseEntity<ErrorResponse> handleBadHeader(badHeaderException ex) {
        log.error("Bad header exception occurred: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
    private String extractUsernameFromPath(String path) {
        int startIdx = path.indexOf("/users/") + "/users/".length();
        int endIdx = path.indexOf("/repos", startIdx);
        if (startIdx >= 0 && endIdx >= 0) {
            return path.substring(startIdx, endIdx);
        }
        return null;
    }


}
