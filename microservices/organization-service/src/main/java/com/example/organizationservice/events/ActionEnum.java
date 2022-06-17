package com.example.organizationservice.events;

public enum ActionEnum {
    GET("GET"), SAVE("SAVE"), UPDATE("UPDATE"), DELETE("DELETE");

    private final String name;

    ActionEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
