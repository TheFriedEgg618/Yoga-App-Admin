// app/src/main/java/com/example/yogaappadmin/model/YogaTypeModel.java
package com.example.yogaappadmin.model;

public class YogaTypeModel {
    private long id;
    private String typeName;
    private String description;

    /**
     * @param id           database primary key
     * @param typeName     the name of the yoga class type (e.g. "Flow Yoga")
     * @param description  optional description of this class type
     */
    public YogaTypeModel(long id, String typeName, String description) {
        this.id          = id;
        this.typeName    = typeName;
        this.description = description;
    }

    // --- Getters ---
    public long   getId()          { return id; }
    public String getTypeName()    { return typeName; }
    public String getDescription() { return description; }
}
