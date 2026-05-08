package com.hw3.department.repository;

import com.hw3.department.config.DepartmentProperties;
import com.hw3.department.model.Choice;
import com.hw3.department.model.Course;
import com.hw3.department.util.SqlNaming;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChoiceRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DepartmentProperties properties;
    private final SqlNaming naming;

    public ChoiceRepository(JdbcTemplate jdbcTemplate, DepartmentProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.naming = new SqlNaming(properties);
    }

    public boolean exists(String studentId, String courseId) {
        String sql = "select count(1) from " + naming.table(properties.getDb().getTables().getChoices())
                + " where " + naming.column(properties.getDb().getColumns().getChoiceStudentId()) + " = ?"
                + " and " + naming.column(properties.getDb().getColumns().getChoiceCourseId()) + " = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, courseId);
        return count != null && count > 0;
    }

    public void insert(Choice choice) {
        String sql = "insert into " + naming.table(properties.getDb().getTables().getChoices()) + " ("
                + naming.column(properties.getDb().getColumns().getChoiceStudentId()) + ", "
                + naming.column(properties.getDb().getColumns().getChoiceCourseId()) + ", "
                + naming.column(properties.getDb().getColumns().getChoiceScore())
                + ") values (?, ?, ?)";
        jdbcTemplate.update(sql, choice.getStudentId(), choice.getCourseId(), choice.getScore());
    }

    public void delete(Choice choice) {
        String sql = "delete from " + naming.table(properties.getDb().getTables().getChoices())
                + " where " + naming.column(properties.getDb().getColumns().getChoiceStudentId()) + " = ?"
                + " and " + naming.column(properties.getDb().getColumns().getChoiceCourseId()) + " = ?";
        jdbcTemplate.update(sql, choice.getStudentId(), choice.getCourseId());
    }

    public List<Course> findSelectedCourses(String studentId) {
        String coursesTable = naming.table(properties.getDb().getTables().getCourses());
        String choicesTable = naming.table(properties.getDb().getTables().getChoices());
        String joinSql = "select "
            + "c." + naming.column(properties.getDb().getColumns().getCourseId()) + " as id, "
            + "c." + naming.column(properties.getDb().getColumns().getCourseName()) + " as name, "
            + "c." + naming.column(properties.getDb().getColumns().getCourseTime()) + " as time, "
            + "c." + naming.column(properties.getDb().getColumns().getCourseScore()) + " as score, "
            + "c." + naming.column(properties.getDb().getColumns().getCourseTeacher()) + " as teacher, "
            + "c." + naming.column(properties.getDb().getColumns().getCourseLocation()) + " as location, "
            + "c." + naming.column(properties.getDb().getColumns().getCourseShare()) + " as share"
                + " from " + coursesTable + " c join " + choicesTable + " ch on "
                + "c." + naming.column(properties.getDb().getColumns().getCourseId())
                + " = ch." + naming.column(properties.getDb().getColumns().getChoiceCourseId())
                + " where ch." + naming.column(properties.getDb().getColumns().getChoiceStudentId()) + " = ?";
        return jdbcTemplate.query(joinSql, (rs, row) -> {
            Course course = new Course();
            course.setId(rs.getString("id"));
            course.setName(rs.getString("name"));
            course.setTime(rs.getString("time"));
            course.setScore(rs.getString("score"));
            course.setTeacher(rs.getString("teacher"));
            course.setLocation(rs.getString("location"));
            course.setShare(rs.getString("share"));
            return course;
        }, studentId);
    }
}
