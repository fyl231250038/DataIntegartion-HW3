package com.hw3.department.controller;

import com.hw3.department.model.BasicResponse;
import com.hw3.department.model.ChoiceRequest;
import com.hw3.department.model.Course;
import com.hw3.department.service.LocalCourseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/local")
public class LocalCourseController {

    private final LocalCourseService localCourseService;

    public LocalCourseController(LocalCourseService localCourseService) {
        this.localCourseService = localCourseService;
    }

    @GetMapping("/classes")
    public List<Course> listClasses() {
        return localCourseService.listClasses();
    }

    @GetMapping("/choices")
    public List<Course> listSelectedCourses() {
        return localCourseService.listSelectedCourses();
    }

    @PostMapping("/choices")
    public BasicResponse selectCourse(@Valid @RequestBody ChoiceRequest request) {
        return localCourseService.selectCourse(request);
    }

    @DeleteMapping("/choices")
    public BasicResponse dropCourse(@Valid @RequestBody ChoiceRequest request) {
        return localCourseService.dropCourse(request);
    }
}
