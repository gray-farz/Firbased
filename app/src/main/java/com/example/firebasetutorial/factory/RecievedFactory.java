package com.example.firebasetutorial.factory;

import com.example.firebasetutorial.Note.Note;
import com.example.firebasetutorial.imageLoad.UploadModel;
import com.example.firebasetutorial.model.Users;

public class RecievedFactory
{
    public static Datas createUploadImage(String fileName, String link, String id)
    {
        return new UploadModel(fileName,link,id);
    }
    public static Datas createUsers(String txtPass, String txtEmail)
    {
        return new Users("new",txtPass,txtEmail,"");
    }
    public static Datas createNote(String id,String title, String desc, int priority)
    {
        return new Note(id,title,desc,priority);
    }
}
