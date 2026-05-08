package com.hw3.department.integration;

public enum DepartmentCode {
    A,
    B,
    C,
    INTEGRATION,
    ALL;

    public static DepartmentCode from(String value) {
        return value == null ? null : DepartmentCode.valueOf(value.trim().toUpperCase());
    }

    public boolean isAcademicDepartment() {
        return this == A || this == B || this == C;
    }
}
