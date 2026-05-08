package com.hw3.department.repository;

import com.hw3.department.config.DepartmentProperties;
import com.hw3.department.model.Course;
import com.hw3.department.util.SqlNaming;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CourseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DepartmentProperties properties;
    private final SqlNaming naming;

    public CourseRepository(JdbcTemplate jdbcTemplate, DepartmentProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.naming = new SqlNaming(properties);
    }

    public List<Course> findAll() {
        String sql = "select "
                + naming.columnAlias(properties.getDb().getColumns().getCourseId(), "id") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseName(), "name") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseTime(), "time") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseScore(), "score") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseTeacher(), "teacher") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseLocation(), "location") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseShare(), "share")
                + " from " + naming.table(properties.getDb().getTables().getCourses());
        return jdbcTemplate.query(sql, (rs, row) -> {
            Course course = new Course();
            course.setId(rs.getString("id"));
            course.setName(rs.getString("name"));
            course.setTime(rs.getString("time"));
            course.setScore(rs.getString("score"));
            course.setTeacher(rs.getString("teacher"));
            course.setLocation(rs.getString("location"));
            course.setShare(rs.getString("share"));
            return course;
        });
    }

    public List<Course> findShared() {
        String sql = "select "
                + naming.columnAlias(properties.getDb().getColumns().getCourseId(), "id") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseName(), "name") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseTime(), "time") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseScore(), "score") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseTeacher(), "teacher") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseLocation(), "location") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getCourseShare(), "share")
                + " from " + naming.table(properties.getDb().getTables().getCourses())
                + " where " + naming.column(properties.getDb().getColumns().getCourseShare()) + " = ?";
        return jdbcTemplate.query(sql, (rs, row) -> {
            Course course = new Course();
            course.setId(rs.getString("id"));
            course.setName(rs.getString("name"));
            course.setTime(rs.getString("time"));
            course.setScore(rs.getString("score"));
            course.setTeacher(rs.getString("teacher"));
            course.setLocation(rs.getString("location"));
            course.setShare(rs.getString("share"));
            return course;
        }, properties.getDb().getShareYesValue());
    }
}
