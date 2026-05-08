package com.hw3.department.integration;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegrationResponse {

    private final String requestId;
    private final DepartmentCode sourceSystem;
    private final DepartmentCode targetSystem;
    private final ActionType action;
    private final Instant timestamp;
    private final ResponseStatus status;
    private final String message;
    private final Map<String, String> bodyFragments = new LinkedHashMap<>();

    public IntegrationResponse(
            String requestId,
            DepartmentCode sourceSystem,
            DepartmentCode targetSystem,
            ActionType action,
            Instant timestamp,
            ResponseStatus status,
            String message) {
        this.requestId = requestId;
        this.sourceSystem = sourceSystem;
        this.targetSystem = targetSystem;
        this.action = action;
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public static IntegrationResponse success(
            String requestId,
            DepartmentCode sourceSystem,
            DepartmentCode targetSystem,
            ActionType action,
            String message) {
        return new IntegrationResponse(
                requestId,
                sourceSystem,
                targetSystem,
                action,
                Instant.now(),
                ResponseStatus.SUCCESS,
                message);
    }

    public static IntegrationResponse failure(
            String requestId,
            DepartmentCode sourceSystem,
            DepartmentCode targetSystem,
            ActionType action,
            String message) {
        return new IntegrationResponse(
                requestId,
                sourceSystem,
                targetSystem,
                action,
                Instant.now(),
                ResponseStatus.FAILURE,
                message);
    }

    public String getRequestId() {
        return requestId;
    }

    public DepartmentCode getSourceSystem() {
        return sourceSystem;
    }

    public DepartmentCode getTargetSystem() {
        return targetSystem;
    }

    public ActionType getAction() {
        return action;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void addBodyFragment(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return;
        }
        bodyFragments.put("fragment-" + bodyFragments.size(), xml);
    }

    public Map<String, String> getBodyFragments() {
        return bodyFragments;
    }
}
