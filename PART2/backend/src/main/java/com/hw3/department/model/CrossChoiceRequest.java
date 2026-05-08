package com.hw3.department.model;

import jakarta.validation.constraints.NotBlank;

public class CrossChoiceRequest {

    @NotBlank
    private String courseId;

    @NotBlank
    private String targetSystem;


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }
}
