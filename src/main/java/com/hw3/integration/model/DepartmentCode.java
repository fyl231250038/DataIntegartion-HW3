package com.hw3.integration.model;

import com.hw3.integration.exception.BadRequestException;

public enum DepartmentCode {
    A,
    B,
    C,
    INTEGRATION,
    ALL;

    public static DepartmentCode from(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("请求中缺少院系标识");
        }
        try {
            return DepartmentCode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("未知院系标识: " + value);
        }
    }

    public boolean isAcademicDepartment() {
        return this == A || this == B || this == C;
    }
}
