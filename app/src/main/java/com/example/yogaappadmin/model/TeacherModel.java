// TeacherModel.java
package com.example.yogaappadmin.model;

public class TeacherModel {
    private long   id;
    private String name;
    private String bio;
    private String classesCsv;

    /** Required by Firebase */
    public TeacherModel() { }

    public TeacherModel(long id, String name, String bio, String classesCsv) {
        this.id         = id;
        this.name       = name;
        this.bio        = bio;
        this.classesCsv = classesCsv;
    }

    // Getters
    public long   getId()         { return id; }
    public String getName()       { return name; }
    public String getBio()        { return bio; }
    public String getClassesCsv() { return classesCsv; }
}
