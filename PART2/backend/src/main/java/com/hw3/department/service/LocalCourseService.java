package com.hw3.department.service;

import com.hw3.department.model.BasicResponse;
import com.hw3.department.model.Choice;
import com.hw3.department.model.ChoiceRequest;
import com.hw3.department.model.Course;
import com.hw3.department.repository.ChoiceRepository;
import com.hw3.department.repository.CourseRepository;
import com.hw3.department.security.AuthContext;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocalCourseService {

    private final CourseRepository courseRepository;
    private final ChoiceRepository choiceRepository;
    private final AuthContext authContext;

    public LocalCourseService(CourseRepository courseRepository,
                              ChoiceRepository choiceRepository,
                              AuthContext authContext) {
        this.courseRepository = courseRepository;
        this.choiceRepository = choiceRepository;
        this.authContext = authContext;
    }

    public List<Course> listClasses() {
        return courseRepository.findAll();
    }

    public List<Course> listSelectedCourses() {
        String studentId = authContext.requireStudentId();
        return choiceRepository.findSelectedCourses(studentId);
    }

    public BasicResponse selectCourse(ChoiceRequest request) {
        String studentId = authContext.requireStudentId();
        Choice choice = new Choice();
        choice.setStudentId(studentId);
        choice.setCourseId(request.getCourseId());
        if (choiceRepository.exists(choice.getStudentId(), choice.getCourseId())) {
            return new BasicResponse("FAILURE", "already selected");
        }
        choiceRepository.insert(choice);
        return new BasicResponse("SUCCESS", "selected");
    }

    public BasicResponse dropCourse(ChoiceRequest request) {
        String studentId = authContext.requireStudentId();
        Choice choice = new Choice();
        choice.setStudentId(studentId);
        choice.setCourseId(request.getCourseId());
        choiceRepository.delete(choice);
        return new BasicResponse("SUCCESS", "dropped");
    }
}
