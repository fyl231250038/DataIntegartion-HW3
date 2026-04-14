package com.hw3.integration.controller;

import com.hw3.integration.service.IntegrationOrchestrator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integration")
public class IntegrationController {

    private final IntegrationOrchestrator integrationOrchestrator;

    public IntegrationController(IntegrationOrchestrator integrationOrchestrator) {
        this.integrationOrchestrator = integrationOrchestrator;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("integration-server-up");
    }

    @PostMapping(
            value = "/requests",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleRequest(@RequestBody String rawXml) {
        return ResponseEntity.ok(integrationOrchestrator.handle(rawXml));
    }
}
