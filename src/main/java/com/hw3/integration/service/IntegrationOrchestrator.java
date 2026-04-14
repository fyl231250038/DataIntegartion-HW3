package com.hw3.integration.service;

import com.hw3.integration.config.IntegrationProperties;
import com.hw3.integration.exception.BadRequestException;
import com.hw3.integration.exception.RemoteSystemException;
import com.hw3.integration.model.ActionType;
import com.hw3.integration.model.DepartmentCode;
import com.hw3.integration.model.IntegrationRequest;
import com.hw3.integration.model.IntegrationResponse;
import com.hw3.integration.model.PayloadType;
import com.hw3.integration.model.ResponseStatus;
import com.hw3.integration.util.XmlEnvelopeParser;
import com.hw3.integration.util.XmlMessageBuilder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IntegrationOrchestrator {

    private final IntegrationProperties integrationProperties;
    private final XmlIntegrationEngine xmlIntegrationEngine;
    private final DepartmentGateway departmentGateway;
    private final XmlEnvelopeParser xmlEnvelopeParser;
    private final XmlMessageBuilder xmlMessageBuilder;

    public IntegrationOrchestrator(
            IntegrationProperties integrationProperties,
            XmlIntegrationEngine xmlIntegrationEngine,
            DepartmentGateway departmentGateway,
            XmlEnvelopeParser xmlEnvelopeParser,
            XmlMessageBuilder xmlMessageBuilder) {
        this.integrationProperties = integrationProperties;
        this.xmlIntegrationEngine = xmlIntegrationEngine;
        this.departmentGateway = departmentGateway;
        this.xmlEnvelopeParser = xmlEnvelopeParser;
        this.xmlMessageBuilder = xmlMessageBuilder;
    }

    public String handle(String rawXml) {
        IntegrationRequest request = null;
        try {
            xmlIntegrationEngine.validateRequestEnvelope(rawXml);
            request = xmlEnvelopeParser.parseRequest(rawXml);
            IntegrationResponse response = switch (request.getAction()) {
                case COURSE_SHARE -> handleCourseShare(request);
                case COURSE_SELECT -> handleCourseMutation(request);
                case COURSE_DROP -> handleCourseMutation(request);
                case QUERY_SHARED_COURSES -> throw new BadRequestException("集成服务器不接受外部直接发来的 QUERY_SHARED_COURSES 请求");
            };
            String responseXml = xmlMessageBuilder.buildResponse(response);
            xmlIntegrationEngine.validateResponseEnvelope(responseXml);
            return responseXml;
        } catch (Exception exception) {
            if (request == null) {
                throw exception;
            }
            IntegrationResponse failure = IntegrationResponse.failure(
                    request.getRequestId(),
                    DepartmentCode.INTEGRATION,
                    request.getSourceSystem(),
                    request.getAction(),
                    exception.getMessage());
            String responseXml = xmlMessageBuilder.buildResponse(failure);
            xmlIntegrationEngine.validateResponseEnvelope(responseXml);
            return responseXml;
        }
    }

    private IntegrationResponse handleCourseShare(IntegrationRequest request) {
        List<DepartmentCode> targets = resolveShareTargets(request);
        if (targets.isEmpty()) {
            throw new BadRequestException("没有可供聚合的目标院系");
        }

        List<String> canonicalClasses = new ArrayList<>();
        for (DepartmentCode target : targets) {
            IntegrationRequest downstream = new IntegrationRequest(
                    generateChildRequestId(request.getRequestId(), target),
                    DepartmentCode.INTEGRATION,
                    target,
                    request.getSourceSystem(),
                    ActionType.QUERY_SHARED_COURSES,
                    Instant.now());
            String downstreamRequestXml = xmlMessageBuilder.buildRequest(downstream);
            String downstreamResponseXml = departmentGateway.send(target, downstreamRequestXml);
            xmlIntegrationEngine.validateResponseEnvelope(downstreamResponseXml);
            IntegrationResponse downstreamResponse = xmlEnvelopeParser.parseResponse(downstreamResponseXml);
            if (downstreamResponse.getStatus() != ResponseStatus.SUCCESS) {
                throw new RemoteSystemException(target + " 院系返回失败: " + downstreamResponse.getMessage());
            }
            String classXml = xmlEnvelopeParser.requireBodyFragment(downstreamResponseXml, PayloadType.CLASS);
            xmlIntegrationEngine.validateLocal(classXml, PayloadType.CLASS, target);
            String canonicalClassXml = xmlIntegrationEngine.toUnified(classXml, PayloadType.CLASS, target);
            xmlIntegrationEngine.validateUnified(canonicalClassXml, PayloadType.CLASS);
            canonicalClasses.add(canonicalClassXml);
        }

        String mergedCanonicalXml = xmlIntegrationEngine.mergeUnifiedDocuments(PayloadType.CLASS, canonicalClasses);
        xmlIntegrationEngine.validateUnified(mergedCanonicalXml, PayloadType.CLASS);
        String sourceFormattedXml = xmlIntegrationEngine.fromUnified(
                mergedCanonicalXml,
                PayloadType.CLASS,
                request.getSourceSystem());

        IntegrationResponse response = IntegrationResponse.success(
                request.getRequestId(),
                DepartmentCode.INTEGRATION,
                request.getSourceSystem(),
                ActionType.COURSE_SHARE,
                "课程共享聚合完成");
        response.addBodyFragment(PayloadType.CLASS, sourceFormattedXml);
        return response;
    }

    private IntegrationResponse handleCourseMutation(IntegrationRequest request) {
        DepartmentCode target = request.getTargetSystem();
        if (target == null || !target.isAcademicDepartment()) {
            throw new BadRequestException("跨系选课/退课必须指定合法的目标院系");
        }
        if (target == request.getSourceSystem()) {
            throw new BadRequestException("本地选课/退课不需要经过集成服务器");
        }

        String studentXml = request.getRequiredBodyFragment(PayloadType.STUDENT);
        String choiceXml = request.getRequiredBodyFragment(PayloadType.CHOICE);

        String canonicalStudentXml = xmlIntegrationEngine.toUnified(studentXml, PayloadType.STUDENT, request.getSourceSystem());
        String canonicalChoiceXml = xmlIntegrationEngine.toUnified(choiceXml, PayloadType.CHOICE, request.getSourceSystem());
        String targetStudentXml = xmlIntegrationEngine.fromUnified(canonicalStudentXml, PayloadType.STUDENT, target);
        String targetChoiceXml = xmlIntegrationEngine.fromUnified(canonicalChoiceXml, PayloadType.CHOICE, target);

        IntegrationRequest downstream = new IntegrationRequest(
                generateChildRequestId(request.getRequestId(), target),
                DepartmentCode.INTEGRATION,
                target,
                request.getSourceSystem(),
                request.getAction(),
                Instant.now());
        downstream.addBodyFragment(PayloadType.STUDENT, targetStudentXml);
        downstream.addBodyFragment(PayloadType.CHOICE, targetChoiceXml);

        String downstreamRequestXml = xmlMessageBuilder.buildRequest(downstream);
        String downstreamResponseXml = departmentGateway.send(target, downstreamRequestXml);
        xmlIntegrationEngine.validateResponseEnvelope(downstreamResponseXml);
        IntegrationResponse downstreamResponse = xmlEnvelopeParser.parseResponse(downstreamResponseXml);

        if (downstreamResponse.getStatus() != ResponseStatus.SUCCESS) {
            return IntegrationResponse.failure(
                    request.getRequestId(),
                    DepartmentCode.INTEGRATION,
                    request.getSourceSystem(),
                    request.getAction(),
                    downstreamResponse.getMessage());
        }

        return IntegrationResponse.success(
                request.getRequestId(),
                DepartmentCode.INTEGRATION,
                request.getSourceSystem(),
                request.getAction(),
                downstreamResponse.getMessage() == null || downstreamResponse.getMessage().isBlank()
                        ? "跨系请求处理成功"
                        : downstreamResponse.getMessage());
    }

    private List<DepartmentCode> resolveShareTargets(IntegrationRequest request) {
        if (request.getTargetSystem() != null && request.getTargetSystem().isAcademicDepartment()) {
            if (request.getTargetSystem() == request.getSourceSystem()) {
                throw new BadRequestException("课程共享目标院系不能与源院系相同");
            }
            return List.of(request.getTargetSystem());
        }
        List<DepartmentCode> targets = new ArrayList<>();
        for (DepartmentCode departmentCode : integrationProperties.getAcademicDepartments()) {
            if (departmentCode != request.getSourceSystem()) {
                targets.add(departmentCode);
            }
        }
        return targets;
    }

    private String generateChildRequestId(String requestId, DepartmentCode target) {
        return requestId + "-" + target.name() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
