// YogaTypeModel.java
package com.example.yogaappadmin.model;

public class YogaTypeModel {
    private long   id;
    private String typeName;
    private String description;

    /** Required by Firebase */
    public YogaTypeModel() { }

    public YogaTypeModel(long id, String typeName, String description) {
        this.id          = id;
        this.typeName    = typeName;
        this.description = description;
    }

    // Getters
    public long   getId()          { return id; }
    public String getTypeName()    { return typeName; }
    public String getDescription() { return description; }
}
