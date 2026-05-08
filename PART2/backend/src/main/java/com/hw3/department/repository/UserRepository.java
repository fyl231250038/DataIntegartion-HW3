package com.hw3.department.repository;

import com.hw3.department.config.DepartmentProperties;
import com.hw3.department.util.SqlNaming;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DepartmentProperties properties;
    private final SqlNaming naming;

    public UserRepository(JdbcTemplate jdbcTemplate, DepartmentProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.naming = new SqlNaming(properties);
    }

    public boolean validate(String username, String password) {
        String sql = "select count(1) from " + naming.table(properties.getDb().getTables().getUsers())
                + " where " + naming.column(properties.getDb().getColumns().getUserName()) + " = ?"
                + " and " + naming.column(properties.getDb().getColumns().getUserPassword()) + " = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password);
        return count != null && count > 0;
    }
}
