package com.hw3.department.model;

import jakarta.validation.constraints.NotBlank;

public class ChoiceRequest {

    @NotBlank
    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
