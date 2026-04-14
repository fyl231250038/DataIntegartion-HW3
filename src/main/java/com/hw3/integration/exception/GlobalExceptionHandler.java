package com.hw3.integration.exception;

import com.hw3.integration.model.DepartmentCode;
import com.hw3.integration.model.IntegrationResponse;
import com.hw3.integration.util.XmlMessageBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final XmlMessageBuilder xmlMessageBuilder;

    public GlobalExceptionHandler(XmlMessageBuilder xmlMessageBuilder) {
        this.xmlMessageBuilder = xmlMessageBuilder;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({IntegrationException.class, RemoteSystemException.class})
    public ResponseEntity<String> handleIntegrationError(RuntimeException exception) {
        return build(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOther(Exception exception) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ResponseEntity<String> build(HttpStatus status, String message) {
        IntegrationResponse response = IntegrationResponse.failure(
                "UNKNOWN",
                DepartmentCode.INTEGRATION,
                null,
                null,
                message);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_XML)
                .body(xmlMessageBuilder.buildResponse(response));
    }
}
