package com.hw3.department.util;

import com.hw3.department.config.DepartmentProperties;

public class SqlNaming {

    private final String quotePrefix;
    private final String quoteSuffix;

    public SqlNaming(DepartmentProperties departmentProperties) {
        this.quotePrefix = departmentProperties.getDb().getQuotePrefix();
        this.quoteSuffix = departmentProperties.getDb().getQuoteSuffix();
    }

    public String table(String name) {
        return quote(name);
    }

    public String column(String name) {
        return quote(name);
    }

    public String columnAlias(String name, String alias) {
        return quote(name) + " as " + alias;
    }

    private String quote(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        if (quotePrefix == null || quotePrefix.trim().isEmpty()) {
            return name;
        }
        return quotePrefix + name + quoteSuffix;
    }
}
