package com.hw3.integration.config;

import com.hw3.integration.exception.BadRequestException;
import com.hw3.integration.model.DepartmentCode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration")
public class IntegrationProperties {

    private Map<String, DepartmentConfig> departments = new LinkedHashMap<>();

    public Map<String, DepartmentConfig> getDepartments() {
        return departments;
    }

    public void setDepartments(Map<String, DepartmentConfig> departments) {
        this.departments = departments;
    }

    public DepartmentConfig getDepartment(DepartmentCode departmentCode) {
        DepartmentConfig config = departments.get(departmentCode.name());
        if (config == null) {
            throw new BadRequestException("未配置院系 " + departmentCode + " 的接口信息");
        }
        return config;
    }

    public List<DepartmentCode> getAcademicDepartments() {
        List<DepartmentCode> departmentCodes = new ArrayList<>();
        for (String code : departments.keySet()) {
            DepartmentCode departmentCode = DepartmentCode.from(code);
            if (departmentCode.isAcademicDepartment()) {
                departmentCodes.add(departmentCode);
            }
        }
        return departmentCodes;
    }

    public static class DepartmentConfig {

        private String endpoint;
        private String studentSchema;
        private String classSchema;
        private String choiceSchema;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getStudentSchema() {
            return studentSchema;
        }

        public void setStudentSchema(String studentSchema) {
            this.studentSchema = studentSchema;
        }

        public String getClassSchema() {
            return classSchema;
        }

        public void setClassSchema(String classSchema) {
            this.classSchema = classSchema;
        }

        public String getChoiceSchema() {
            return choiceSchema;
        }

        public void setChoiceSchema(String choiceSchema) {
            this.choiceSchema = choiceSchema;
        }
    }
}
