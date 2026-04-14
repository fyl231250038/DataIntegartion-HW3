package com.hw3.integration.model;

public enum PayloadType {
    STUDENT("students", "student"),
    CLASS("classes", "class"),
    CHOICE("choices", "choice");

    private final String rootName;
    private final String itemName;

    PayloadType(String rootName, String itemName) {
        this.rootName = rootName;
        this.itemName = itemName;
    }

    public String rootName() {
        return rootName;
    }

    public String itemName() {
        return itemName;
    }
}
