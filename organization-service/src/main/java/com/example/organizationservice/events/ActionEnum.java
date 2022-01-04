package com.example.organizationservice.events;

public enum ActionEnum {
    GET("GET"), SAVE("SAVE"), UPDATE("UPDATE"), DELETE("DELETE");

    private String name;


    ActionEnum(String get) {
    }

    @Override
    public String toString() {
        return this.name;
    }
}
