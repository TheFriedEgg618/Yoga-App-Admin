package com.example.yogaappadmin.model;

/**
 * Simple model representing a yoga class type.
 */
public class YogaTypeModel {
    private final String name;
    private final String description;

    public YogaTypeModel(String name, String description) {
        this.name        = name;
        this.description = description;
    }

    /** Friendly display name of the yoga class type (e.g. "Flow Yoga") */
    public String getName() {
        return name;
    }

    /** Short description of what this class involves */
    public String getDescription() {
        return description;
    }
}
