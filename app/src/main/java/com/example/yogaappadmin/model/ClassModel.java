    package com.example.yogaappadmin.model;

    public class ClassModel {
        private long   id;
        private String title;
        private String teacherName;
        private String day;
        private String time;
        private int    capacity;
        private int    duration;
        private double price;
        private String type;
        private String description;

        /**
         * @param id           database primary key
         * @param title        the class title
         * @param teacherName  the teacher’s name
         * @param day          comma‑separated day abbreviations (e.g. "Mon,Wed,Fri")
         * @param time         time string ("HH:mm")
         * @param capacity     maximum number of students
         * @param duration     length in minutes
         * @param price        cost, in your currency
         * @param type         yoga type name
         * @param description  optional description
         */
        public ClassModel(long id,
                          String title,
                          String teacherName,
                          String day,
                          String time,
                          int capacity,
                          int duration,
                          double price,
                          String type,
                          String description) {
            this.id           = id;
            this.title        = title;
            this.teacherName  = teacherName;
            this.day          = day;
            this.time         = time;
            this.capacity     = capacity;
            this.duration     = duration;
            this.price        = price;
            this.type         = type;
            this.description  = description;
        }

        // --- Getters ---

        public long   getId()            { return id; }
        public String getTitle()         { return title; }
        public String getTeacherName()   { return teacherName; }
        public String getDay()           { return day; }
        public String getTime()          { return time; }
        public int    getCapacity()      { return capacity; }
        public int    getDuration()      { return duration; }
        public double getPrice()         { return price; }
        public String getType()          { return type; }
        public String getDescription()   { return description; }
    }
