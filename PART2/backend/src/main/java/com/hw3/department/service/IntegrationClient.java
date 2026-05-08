package com.hw3.department.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IntegrationClient {

    private final RestTemplate restTemplate;
    private final String serverBaseUrl;
    private final String requestPath;

    public IntegrationClient(RestTemplate restTemplate,
                             @Value("${integration.serverBaseUrl}") String serverBaseUrl,
                             @Value("${integration.requestPath}") String requestPath) {
        this.restTemplate = restTemplate;
        this.serverBaseUrl = serverBaseUrl;
        this.requestPath = requestPath;
    }

    public String sendIntegrationRequest(String xmlPayload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<>(xmlPayload, headers);
        return restTemplate.postForObject(serverBaseUrl + requestPath, entity, String.class);
    }
}
