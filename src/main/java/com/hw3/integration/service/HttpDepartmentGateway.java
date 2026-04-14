package com.hw3.integration.service;

import com.hw3.integration.config.IntegrationProperties;
import com.hw3.integration.exception.RemoteSystemException;
import com.hw3.integration.model.DepartmentCode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpDepartmentGateway implements DepartmentGateway {

    private final RestTemplate restTemplate;
    private final IntegrationProperties integrationProperties;

    public HttpDepartmentGateway(RestTemplate restTemplate, IntegrationProperties integrationProperties) {
        this.restTemplate = restTemplate;
        this.integrationProperties = integrationProperties;
    }

    @Override
    public String send(DepartmentCode targetDepartment, String requestXml) {
        String endpoint = integrationProperties.getDepartment(targetDepartment).getEndpoint();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        try {
            String response = restTemplate.postForObject(endpoint, new HttpEntity<>(requestXml, headers), String.class);
            if (response == null || response.isBlank()) {
                throw new RemoteSystemException(targetDepartment + " 院系返回了空响应");
            }
            return response;
        } catch (RestClientException exception) {
            throw new RemoteSystemException("调用 " + targetDepartment + " 院系接口失败: " + exception.getMessage(), exception);
        }
    }
}
