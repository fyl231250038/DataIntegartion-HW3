package com.hw3.department.repository;

import com.hw3.department.config.DepartmentProperties;
import com.hw3.department.model.Student;
import com.hw3.department.util.SqlNaming;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DepartmentProperties properties;
    private final SqlNaming naming;

    public StudentRepository(JdbcTemplate jdbcTemplate, DepartmentProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.naming = new SqlNaming(properties);
    }

    public boolean existsById(String studentId) {
        String sql = "select count(1) from " + naming.table(properties.getDb().getTables().getStudents())
                + " where " + naming.column(properties.getDb().getColumns().getStudentId()) + " = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null && count > 0;
    }

    public Student findById(String studentId) {
        String sql = "select "
                + naming.columnAlias(properties.getDb().getColumns().getStudentId(), "id") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getStudentName(), "name") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getStudentSex(), "sex") + ", "
                + naming.columnAlias(properties.getDb().getColumns().getStudentMajor(), "major")
                + " from " + naming.table(properties.getDb().getTables().getStudents())
                + " where " + naming.column(properties.getDb().getColumns().getStudentId()) + " = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, row) -> {
                Student student = new Student();
                student.setId(rs.getString("id"));
                student.setName(rs.getString("name"));
                student.setSex(rs.getString("sex"));
                student.setMajor(rs.getString("major"));
                return student;
            }, studentId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public void insert(Student student) {
        String sql = "insert into " + naming.table(properties.getDb().getTables().getStudents()) + " ("
                + naming.column(properties.getDb().getColumns().getStudentId()) + ", "
                + naming.column(properties.getDb().getColumns().getStudentName()) + ", "
                + naming.column(properties.getDb().getColumns().getStudentSex()) + ", "
                + naming.column(properties.getDb().getColumns().getStudentMajor())
                + ") values (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                student.getId(),
                student.getName(),
                student.getSex(),
                student.getMajor());
    }
}
