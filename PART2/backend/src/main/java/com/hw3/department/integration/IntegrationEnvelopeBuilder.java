package com.hw3.department.integration;

import java.time.format.DateTimeFormatter;

public class IntegrationEnvelopeBuilder {

    public String buildRequest(IntegrationRequest request) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<integrationRequest>");
        xml.append("<header>");
        append(xml, "requestId", request.getRequestId());
        append(xml, "sourceSystem", request.getSourceSystem() == null ? null : request.getSourceSystem().name());
        append(xml, "targetSystem", request.getTargetSystem() == null ? null : request.getTargetSystem().name());
        append(xml, "originSystem", request.getOriginSystem() == null ? null : request.getOriginSystem().name());
        append(xml, "action", request.getAction() == null ? null : request.getAction().name());
        append(xml, "timestamp", request.getTimestamp() == null ? null : DateTimeFormatter.ISO_INSTANT.format(request.getTimestamp()));
        xml.append("</header>");
        xml.append("<body>");
        if (request.getBodyFragment("students") != null) {
            xml.append(request.getBodyFragment("students"));
        }
        if (request.getBodyFragment("choices") != null) {
            xml.append(request.getBodyFragment("choices"));
        }
        if (request.getBodyFragment("classes") != null) {
            xml.append(request.getBodyFragment("classes"));
        }
        xml.append("</body>");
        xml.append("</integrationRequest>");
        return xml.toString();
    }

    public String buildResponse(IntegrationResponse response) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<integrationResponse>");
        xml.append("<header>");
        append(xml, "requestId", response.getRequestId());
        append(xml, "sourceSystem", response.getSourceSystem() == null ? null : response.getSourceSystem().name());
        append(xml, "targetSystem", response.getTargetSystem() == null ? null : response.getTargetSystem().name());
        append(xml, "action", response.getAction() == null ? null : response.getAction().name());
        append(xml, "timestamp", response.getTimestamp() == null ? null : DateTimeFormatter.ISO_INSTANT.format(response.getTimestamp()));
        xml.append("</header>");
        append(xml, "status", response.getStatus() == null ? null : response.getStatus().name());
        append(xml, "message", response.getMessage());
        xml.append("<body>");
        response.getBodyFragments().values().forEach(xml::append);
        xml.append("</body>");
        xml.append("</integrationResponse>");
        return xml.toString();
    }

    private void append(StringBuilder xml, String tagName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        xml.append('<').append(tagName).append('>');
        xml.append(escape(value));
        xml.append("</").append(tagName).append('>');
    }

    private String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
