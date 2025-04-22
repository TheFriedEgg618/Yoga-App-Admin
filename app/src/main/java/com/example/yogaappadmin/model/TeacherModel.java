package com.example.yogaappadmin.model;

public class TeacherModel {
    private final String name;
    private final String bio;
    private final String classesCsv;

    public TeacherModel(String name, String bio, String classesCsv) {
        this.name       = name;
        this.bio        = bio;
        this.classesCsv = classesCsv;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getClassesCsv() {
        return classesCsv;
    }
}
