package com.example.firebasetutorial.imageLoad;

import com.example.firebasetutorial.factory.Datas;

public class UploadModel implements Datas
{
    String title,link,id;

    public UploadModel()
    {

    }

    public UploadModel(String title, String link, String id) {
        this.title = title;
        this.link = link;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
