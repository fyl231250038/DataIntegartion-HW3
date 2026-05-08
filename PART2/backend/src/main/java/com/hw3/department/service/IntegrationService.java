package com.hw3.department.service;

import com.hw3.department.config.DepartmentProperties;
import com.hw3.department.integration.ActionType;
import com.hw3.department.integration.DepartmentCode;
import com.hw3.department.integration.IntegrationEnvelopeBuilder;
import com.hw3.department.integration.IntegrationEnvelopeParser;
import com.hw3.department.integration.IntegrationRequest;
import com.hw3.department.integration.IntegrationResponse;
import com.hw3.department.integration.IntegrationResponseEnvelope;
import com.hw3.department.integration.ResponseStatus;
import com.hw3.department.model.BasicResponse;
import com.hw3.department.model.Choice;
import com.hw3.department.model.Course;
import com.hw3.department.model.CrossChoiceRequest;
import com.hw3.department.model.Student;
import com.hw3.department.repository.ChoiceRepository;
import com.hw3.department.repository.CourseRepository;
import com.hw3.department.repository.StudentRepository;
import com.hw3.department.security.AuthContext;
import com.hw3.department.util.DepartmentXmlMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IntegrationService {

    private final DepartmentProperties properties;
    private final DepartmentXmlMapper xmlMapper;
    private final IntegrationClient integrationClient;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ChoiceRepository choiceRepository;
    private final AuthContext authContext;
    private final IntegrationEnvelopeParser envelopeParser = new IntegrationEnvelopeParser();
    private final IntegrationEnvelopeBuilder envelopeBuilder = new IntegrationEnvelopeBuilder();

    public IntegrationService(DepartmentProperties properties,
                              DepartmentXmlMapper xmlMapper,
                              IntegrationClient integrationClient,
                              StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              ChoiceRepository choiceRepository,
                              AuthContext authContext) {
        this.properties = properties;
        this.xmlMapper = xmlMapper;
        this.integrationClient = integrationClient;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.choiceRepository = choiceRepository;
        this.authContext = authContext;
    }

    public String handleExternalRequest(String xmlPayload) {
        IntegrationRequest request = envelopeParser.parseRequest(xmlPayload);
        DepartmentCode current = DepartmentCode.from(properties.getCode());
        try {
            if (request.getAction() == ActionType.QUERY_SHARED_COURSES) {
                List<Course> sharedCourses = courseRepository.findShared();
                String classesXml = xmlMapper.buildCoursesXml(sharedCourses);
                IntegrationResponse response = IntegrationResponse.success(
                        request.getRequestId(),
                        current,
                        DepartmentCode.INTEGRATION,
                        request.getAction(),
                        "shared courses ready");
                response.addBodyFragment(classesXml);
                return envelopeBuilder.buildResponse(response);
            }

            if (request.getAction() == ActionType.COURSE_SELECT || request.getAction() == ActionType.COURSE_DROP) {
                String studentXml = request.getBodyFragment(xmlMapper.getXml().getStudent().getRootName());
                String choiceXml = request.getBodyFragment(xmlMapper.getXml().getChoice().getRootName());
                if (studentXml == null || choiceXml == null) {
                    IntegrationResponse failure = IntegrationResponse.failure(
                            request.getRequestId(),
                            current,
                            DepartmentCode.INTEGRATION,
                            request.getAction(),
                            "missing student/choice xml");
                    return envelopeBuilder.buildResponse(failure);
                }
                Student student = xmlMapper.parseStudent(studentXml);
                Choice choice = xmlMapper.parseChoice(choiceXml);
                if (request.getAction() == ActionType.COURSE_SELECT) {
                    if (!studentRepository.existsById(student.getId())) {
                        studentRepository.insert(student);
                    }
                    if (!choiceRepository.exists(choice.getStudentId(), choice.getCourseId())) {
                        choiceRepository.insert(choice);
                    }
                } else {
                    choiceRepository.delete(choice);
                }
                IntegrationResponse ok = IntegrationResponse.success(
                        request.getRequestId(),
                        current,
                        DepartmentCode.INTEGRATION,
                        request.getAction(),
                        "ok");
                return envelopeBuilder.buildResponse(ok);
            }

            IntegrationResponse failure = IntegrationResponse.failure(
                    request.getRequestId(),
                    current,
                    DepartmentCode.INTEGRATION,
                    request.getAction(),
                    "unsupported action");
            return envelopeBuilder.buildResponse(failure);
        } catch (Exception exception) {
            IntegrationResponse failure = IntegrationResponse.failure(
                    request.getRequestId(),
                    current,
                    DepartmentCode.INTEGRATION,
                    request.getAction(),
                    exception.getMessage());
            return envelopeBuilder.buildResponse(failure);
        }
    }

    public List<Course> querySharedCourses(String targetSystem) {
        DepartmentCode target = targetSystem == null || targetSystem.trim().isEmpty()
                ? null
                : DepartmentCode.from(targetSystem);
        IntegrationRequest request = new IntegrationRequest(
                newRequestId(),
                DepartmentCode.from(properties.getCode()),
                target,
                null,
                ActionType.COURSE_SHARE,
                Instant.now());
        String requestXml = envelopeBuilder.buildRequest(request);
        String responseXml = integrationClient.sendIntegrationRequest(requestXml);
        IntegrationResponseEnvelope response = envelopeParser.parseResponse(responseXml);
        if (response.getStatus() != ResponseStatus.SUCCESS) {
            throw new IllegalStateException(response.getMessage() == null ? "integration failed" : response.getMessage());
        }
        String classXml = response.getBodyFragment(xmlMapper.getXml().getCourse().getRootName());
        if (classXml == null) {
            return java.util.Collections.emptyList();
        }
        return xmlMapper.parseCourses(classXml);
    }

    public BasicResponse crossSelect(CrossChoiceRequest request) {
        return sendCrossAction(ActionType.COURSE_SELECT, request);
    }

    public BasicResponse crossDrop(CrossChoiceRequest request) {
        return sendCrossAction(ActionType.COURSE_DROP, request);
    }

    private BasicResponse sendCrossAction(ActionType action, CrossChoiceRequest request) {
        String studentId = authContext.requireStudentId();
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            return new BasicResponse("FAILURE", "student not found");
        }
        Choice choice = new Choice();
        choice.setStudentId(studentId);
        choice.setCourseId(request.getCourseId());
        String studentXml = xmlMapper.buildStudentXml(student);
        String choiceXml = xmlMapper.buildChoiceXml(choice);
        IntegrationRequest integrationRequest = new IntegrationRequest(
                newRequestId(),
                DepartmentCode.from(properties.getCode()),
                DepartmentCode.from(request.getTargetSystem()),
                null,
                action,
                Instant.now());
        integrationRequest.addBodyFragment(xmlMapper.getXml().getStudent().getRootName(), studentXml);
        integrationRequest.addBodyFragment(xmlMapper.getXml().getChoice().getRootName(), choiceXml);
        String requestXml = envelopeBuilder.buildRequest(integrationRequest);
        String responseXml = integrationClient.sendIntegrationRequest(requestXml);
        IntegrationResponseEnvelope response = envelopeParser.parseResponse(responseXml);
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            return new BasicResponse("SUCCESS", response.getMessage() == null ? "ok" : response.getMessage());
        }
        return new BasicResponse("FAILURE", response.getMessage() == null ? "failed" : response.getMessage());
    }

    private String newRequestId() {
        return properties.getCode() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
