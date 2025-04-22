// app/src/main/java/com/example/yogaappadmin/model/TeacherModel.java
package com.example.yogaappadmin.model;

public class TeacherModel {
    private long id;
    private String name;
    private String bio;
    private String classesCsv;

    /**
     * @param id           database primary key
     * @param name         teacher’s full name
     * @param bio          optional biography or notes
     * @param classesCsv   comma‑separated list of class‑type names they teach
     */
    public TeacherModel(long id, String name, String bio, String classesCsv) {
        this.id         = id;
        this.name       = name;
        this.bio        = bio;
        this.classesCsv = classesCsv;
    }

    // --- Getters ---
    public long   getId()         { return id; }
    public String getName()       { return name; }
    public String getBio()        { return bio; }
    public String getClassesCsv() { return classesCsv; }
}
