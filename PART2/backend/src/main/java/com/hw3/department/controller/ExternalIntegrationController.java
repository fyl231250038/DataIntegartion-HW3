package com.hw3.department.controller;

import com.hw3.department.service.IntegrationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external")
public class ExternalIntegrationController {

    private final IntegrationService integrationService;

    public ExternalIntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping(value = "/integration", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String handleIntegration(@RequestBody String xmlPayload) {
        return integrationService.handleExternalRequest(xmlPayload);
    }
}
