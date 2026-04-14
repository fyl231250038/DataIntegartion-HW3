package com.hw3.integration.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hw3.integration.config.IntegrationProperties;
import com.hw3.integration.model.DepartmentCode;
import com.hw3.integration.model.PayloadType;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class XmlIntegrationEngineTest {

    private XmlIntegrationEngine engine;

    @BeforeEach
    void setUp() {
        IntegrationProperties properties = new IntegrationProperties();
        Map<String, IntegrationProperties.DepartmentConfig> departments = new LinkedHashMap<>();
        departments.put("A", department("classpath:schemas/local/studentA.xsd",
                "classpath:schemas/local/classA.xsd",
                "classpath:schemas/local/choiceA.xsd"));
        departments.put("B", department("classpath:schemas/local/studentB.xsd",
                "classpath:schemas/local/classB.xsd",
                "classpath:schemas/local/choiceB.xsd"));
        departments.put("C", department("classpath:schemas/local/studentC.xsd",
                "classpath:schemas/local/classC.xsd",
                "classpath:schemas/local/choiceC.xsd"));
        properties.setDepartments(departments);
        engine = new XmlIntegrationEngine(new DefaultResourceLoader(), properties);
    }

    @Test
    void transformsStudentPayloadAcrossAllDepartments() {
        String localA = """
                <Students>
                  <student>
                    <学号>2024001</学号>
                    <姓名>张三</姓名>
                    <性别>男</性别>
                    <院系>计算机学院</院系>
                  </student>
                </Students>
                """;

        String unified = engine.toUnified(localA, PayloadType.STUDENT, DepartmentCode.A);
        assertTrue(unified.contains("<students>"));
        assertTrue(unified.contains("<id>2024001</id>"));
        assertTrue(unified.contains("<major>计算机学院</major>"));

        String localB = engine.fromUnified(unified, PayloadType.STUDENT, DepartmentCode.B);
        assertTrue(localB.contains("<学号>2024001</学号>"));
        assertTrue(localB.contains("<专业>计算机学院</专业>"));

        String localC = engine.fromUnified(unified, PayloadType.STUDENT, DepartmentCode.C);
        assertTrue(localC.contains("<Sno>2024001</Sno>"));
        assertTrue(localC.contains("<Sde>计算机学院</Sde>"));
    }

    @Test
    void transformsClassPayloadAcrossAllDepartments() {
        String localB = """
                <Classes>
                  <class>
                    <编号>CS101</编号>
                    <名称>数据集成</名称>
                    <课时>32</课时>
                    <学分>2</学分>
                    <老师>李老师</老师>
                    <地点>教一101</地点>
                    <共享>Y</共享>
                  </class>
                </Classes>
                """;

        String unified = engine.toUnified(localB, PayloadType.CLASS, DepartmentCode.B);
        assertTrue(unified.contains("<classes>"));
        assertTrue(unified.contains("<time>32</time>"));
        assertTrue(unified.contains("<share>Y</share>"));

        String localA = engine.fromUnified(unified, PayloadType.CLASS, DepartmentCode.A);
        assertTrue(localA.contains("<课程编号>CS101</课程编号>"));
        assertTrue(localA.contains("<授课地点>教一101</授课地点>"));

        String localC = engine.fromUnified(unified, PayloadType.CLASS, DepartmentCode.C);
        assertTrue(localC.contains("<Cno>CS101</Cno>"));
        assertTrue(localC.contains("<Share>Y</Share>"));
    }

    @Test
    void transformsChoicePayloadAcrossAllDepartments() {
        String localC = """
                <Choices>
                  <choice>
                    <Cno>CS101</Cno>
                    <Sno>2024001</Sno>
                    <Grd>95</Grd>
                  </choice>
                </Choices>
                """;

        String unified = engine.toUnified(localC, PayloadType.CHOICE, DepartmentCode.C);
        assertTrue(unified.contains("<choices>"));
        assertTrue(unified.contains("<sid>2024001</sid>"));
        assertTrue(unified.contains("<cid>CS101</cid>"));

        String localA = engine.fromUnified(unified, PayloadType.CHOICE, DepartmentCode.A);
        assertTrue(localA.contains("<学生编号>2024001</学生编号>"));

        String localB = engine.fromUnified(unified, PayloadType.CHOICE, DepartmentCode.B);
        assertTrue(localB.contains("<得分>95</得分>"));
    }

    @Test
    void mergesUnifiedDocumentsAndKeepsSchemaValid() {
        String first = """
                <classes>
                  <class>
                    <id>A001</id>
                    <name>软件工程</name>
                    <score>3</score>
                    <teacher>王老师</teacher>
                    <location>一教201</location>
                  </class>
                </classes>
                """;
        String second = """
                <classes>
                  <class>
                    <id>B002</id>
                    <name>数据库</name>
                    <time>48</time>
                    <score>3</score>
                    <teacher>赵老师</teacher>
                    <location>二教105</location>
                    <share>Y</share>
                  </class>
                </classes>
                """;

        String merged = engine.mergeUnifiedDocuments(PayloadType.CLASS, java.util.List.of(first, second));
        assertTrue(merged.contains("<id>A001</id>"));
        assertTrue(merged.contains("<id>B002</id>"));
        assertDoesNotThrow(() -> engine.validateUnified(merged, PayloadType.CLASS));
    }

    private IntegrationProperties.DepartmentConfig department(String studentSchema, String classSchema, String choiceSchema) {
        IntegrationProperties.DepartmentConfig config = new IntegrationProperties.DepartmentConfig();
        config.setStudentSchema(studentSchema);
        config.setClassSchema(classSchema);
        config.setChoiceSchema(choiceSchema);
        return config;
    }
}
