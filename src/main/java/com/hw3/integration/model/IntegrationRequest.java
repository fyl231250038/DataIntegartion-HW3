package com.hw3.integration.model;

import com.hw3.integration.exception.BadRequestException;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegrationRequest {

    private final String requestId;
    private final DepartmentCode sourceSystem;
    private final DepartmentCode targetSystem;
    private final DepartmentCode originSystem;
    private final ActionType action;
    private final Instant timestamp;
    private final Map<String, String> bodyFragments = new LinkedHashMap<>();

    public IntegrationRequest(
            String requestId,
            DepartmentCode sourceSystem,
            DepartmentCode targetSystem,
            DepartmentCode originSystem,
            ActionType action,
            Instant timestamp) {
        this.requestId = requestId;
        this.sourceSystem = sourceSystem;
        this.targetSystem = targetSystem;
        this.originSystem = originSystem;
        this.action = action;
        this.timestamp = timestamp;
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

    public DepartmentCode getOriginSystem() {
        return originSystem;
    }

    public ActionType getAction() {
        return action;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void addBodyFragment(PayloadType payloadType, String xml) {
        bodyFragments.put(payloadType.rootName(), xml);
    }

    public void addBodyFragment(String rootName, String xml) {
        bodyFragments.put(rootName.trim().toLowerCase(), xml);
    }

    public String getRequiredBodyFragment(PayloadType payloadType) {
        String xml = bodyFragments.get(payloadType.rootName());
        if (xml == null || xml.isBlank()) {
            throw new BadRequestException("请求体缺少 " + payloadType.rootName() + " XML 数据");
        }
        return xml;
    }

    public Collection<String> getBodyFragments() {
        return bodyFragments.values();
    }
}
