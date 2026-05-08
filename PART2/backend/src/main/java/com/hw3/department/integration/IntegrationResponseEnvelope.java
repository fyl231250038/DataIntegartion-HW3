package com.hw3.department.integration;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegrationResponseEnvelope {

    private final String requestId;
    private final DepartmentCode sourceSystem;
    private final DepartmentCode targetSystem;
    private final ActionType action;
    private final Instant timestamp;
    private final ResponseStatus status;
    private final String message;
    private final Map<String, String> bodyFragments = new LinkedHashMap<>();

    public IntegrationResponseEnvelope(
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

    public void addBodyFragment(String rootName, String xml) {
        if (rootName == null || xml == null) {
            return;
        }
        bodyFragments.put(rootName.trim().toLowerCase(), xml);
    }

    public String getBodyFragment(String rootName) {
        if (rootName == null) {
            return null;
        }
        return bodyFragments.get(rootName.trim().toLowerCase());
    }
}
