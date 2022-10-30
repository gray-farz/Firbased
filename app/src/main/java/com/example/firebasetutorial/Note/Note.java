package com.example.firebasetutorial.Note;


import com.example.firebasetutorial.factory.Datas;

public class Note implements Datas {

    private String id;

    private String title;

    private String description;

    private int priority;

    public Note(String id,String title, String description, int priority) {
        this.id=id;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
