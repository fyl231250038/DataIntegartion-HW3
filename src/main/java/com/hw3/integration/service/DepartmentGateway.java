package com.hw3.integration.service;

import com.hw3.integration.model.DepartmentCode;

public interface DepartmentGateway {

    String send(DepartmentCode targetDepartment, String requestXml);
}
