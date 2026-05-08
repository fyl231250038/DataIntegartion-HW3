package com.hw3.department.controller;

import com.hw3.department.model.BasicResponse;
import com.hw3.department.model.Course;
import com.hw3.department.model.CrossChoiceRequest;
import com.hw3.department.service.IntegrationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cross")
public class CrossDeptController {

    private final IntegrationService integrationService;

    public CrossDeptController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping("/classes")
    public List<Course> listSharedClasses(@RequestParam(value = "target", required = false) String target) {
        return integrationService.querySharedCourses(target);
    }

    @PostMapping("/choices")
    public BasicResponse crossSelect(@Valid @RequestBody CrossChoiceRequest request) {
        return integrationService.crossSelect(request);
    }

    @DeleteMapping("/choices")
    public BasicResponse crossDrop(@Valid @RequestBody CrossChoiceRequest request) {
        return integrationService.crossDrop(request);
    }
}
